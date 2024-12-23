package com.jhpark.simple_chat_room.redis.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSynchronizer {

    private final RedisService redisService;
    private final RedisScript<List> synchronizeRoomScript;

    private String getRoomKey(final Long roomId) {
        return "room:" + roomId + ":participants";
    }

    @Async
    public void addUser(final Long roomId, final Long userId){
        
        final String roomKey = getRoomKey(roomId);
        final String userIdString = userId.toString();

        redisService.save(roomKey, userIdString);
        if(!redisService.isMember(roomKey, userIdString)){
            log.error("REDIS USER ADD FAILED : userId={}, roomId={}",userId,roomId);
            removeRoom(roomId);
            return;
        }

        log.info("REDIS USER ADDED : userId={}, roomId={}", userId, roomId);
    }

    @Async
    public void removeUser(final Long roomId, final Long userId){

        final String roomKey = getRoomKey(roomId);
        final String userIdString = userId.toString();

        redisService.delete(roomKey, userIdString);
        if(redisService.isMember(roomKey, userIdString)){
            log.error("REDIS USER DELETE FAILED : userId={}, roomId={}", userId, roomId);
            removeRoom(roomId);
            return;
        }

        log.info("REDIS USER REMOVED : userId={}, roomId={}", userId, roomId);

        if(redisService.getSetSize(roomKey) == 0){
            log.info("REDIS ROOM IS EMPTY : roomId={}",roomId);
            removeRoom(roomId);
        }


    }

    @Async
    public void removeRoom(final Long roomId){
        removeRoom(roomId,5);
    }

    private void removeRoom(final Long roomId, int retries) {
        final String roomKey = getRoomKey(roomId);

        redisService.deleteKey(roomKey);

        if (redisService.isExistKey(roomKey)) {
            if (retries > 0) {
                log.warn("REDIS ROOM DELETE FAILED : roomId={}, {} times left", roomId, retries);
                removeRoom(roomId, retries - 1);
                return;
            } else {
                log.error("REDIS ROOM DELETE FINALLY FAILED AFTER MAX RETRIES : roomId={}", roomId);
                return;
            }
        }

        log.info("REDIS ROOM DELETED : roomId={}", roomId);
    }

    @Async
    public void synchronizeUser(final Long roomId, List<Long> userIds) {

        final String roomKey = getRoomKey(roomId);

        final List<String> userIdsAsStrings = userIds.stream()
                .sorted((l1,l2) -> Long.compare(l1, l2))
                .map(String::valueOf)
                .toList();

        final List<String> results = redisService.runLuaScript(
            synchronizeRoomScript, 
            List.of(roomKey), 
            userIdsAsStrings);

        if(!results.equals(userIdsAsStrings)){
            log.error("REDIS SYNCHRONIZE FAILED : {}", roomId);
            removeRoom(roomId);
            return;
        }

        log.info("REDIS SYNCHRONIZE SUCCEED : roomId={}",roomId);

    }

    public Set<String> getRedisParticipants(final Long roomId){
        return redisService.get(getRoomKey(roomId));
    }
    
}
