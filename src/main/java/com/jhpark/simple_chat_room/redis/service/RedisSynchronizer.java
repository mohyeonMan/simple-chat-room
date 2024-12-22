package com.jhpark.simple_chat_room.redis.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.script.RedisScript;
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

    public void addUser(final Long roomId, final Long userId){
        
        final String roomKey = getRoomKey(roomId);
        final String userIdString = userId.toString();

        redisService.save(roomKey, userIdString);
        if(!redisService.isMember(roomKey, userIdString)){
            throw new RuntimeException("사용자 추가에 실패하였습니다.");
        }
    }

    //사용자 제거.

    public void removeUser(final Long roomId, final Long userId){

        final String roomKey = getRoomKey(roomId);
        final String userIdString = userId.toString();

        redisService.delete(roomKey, userIdString);
        if(redisService.isMember(roomKey, userIdString)){
            throw new RuntimeException("사용자 삭제에 실패하였습니다.");
        }

        if(redisService.getSetSize(roomKey) == 0){
            removeRoom(roomId);
        }

    }

    public void removeRoom(final Long roomId){

        final String roomKey = getRoomKey(roomId);

        redisService.deleteKey(roomKey);

        if(redisService.isExistKey(roomKey)){
            throw new RuntimeException("채팅방 삭제에 실패했습니다.");
        }
            
    }

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
            log.error("REDIS SYNCHRONIZE FAILED");
            redisService.deleteKey(roomKey);
        }

    }
    
}
