package com.jhpark.simple_chat_room.room.service;

import com.jhpark.simple_chat_room.redis.service.RedisSynchronizer;
import com.jhpark.simple_chat_room.room.dto.response.CreateRoomResponse;
import com.jhpark.simple_chat_room.room.dto.response.InviteUserResponse;
import com.jhpark.simple_chat_room.room.entity.Room;
import com.jhpark.simple_chat_room.room.entity.RoomEntry;
import com.jhpark.simple_chat_room.room.repository.RoomEntryRepository;
import com.jhpark.simple_chat_room.room.repository.RoomRepository;
import com.jhpark.simple_chat_room.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoomService {


    private final RoomValidationService validationService;
    private final RoomRepository roomRepository;
    private final RoomEntryRepository roomEntryRepository;
    private final RedisSynchronizer redisSynchronizer;


    /**
     * 방 생성 및 사용자 추가
     * @param roomName 방 이름
     * @param userIds 추가할 사용자 ID 리스트 (최소 1명 이상이어야 함)
     * @return 생성된 방 정보
     */
    @Transactional
    public CreateRoomResponse createRoom(final String roomName, final List<Long> friendIds) {

        final Long currentUserId = SecurityUtil.getCurrentUserId();

        validationService.validateIsUserInvitedNoOne(friendIds);
        validationService.validateIsUserInvitedItselves(currentUserId, friendIds);
        validationService.validateIsFriends(currentUserId, friendIds);

        final Room savedRoom = roomRepository.save(Room.builder()
        .roomName(roomName)
        .createdAt(LocalDateTime.now())
        .build());
        
        friendIds.add(currentUserId);
        friendIds.stream().distinct().toList().forEach(userId -> {

            roomEntryRepository.save(RoomEntry.builder()
                    .room(savedRoom)
                    .userId(userId)
                    .joinedAt(LocalDateTime.now())
                    .build());

        });

        //redis에 추가.
        redisSynchronizer.synchronizeUser(savedRoom.getId(), friendIds);

        return CreateRoomResponse.builder()
                .roomId(savedRoom.getId())
                .roomName(savedRoom.getRoomName())
                .build();
    }

    /**
     * 방 나가기
     * @param roomId 나갈 방 ID
     */
    @Transactional
    public void leaveRoom(final Long roomId) {

        final Long currentUserId = SecurityUtil.getCurrentUserId();

        final Room room = roomRepository.findByIdAndDeletedAtIsNull(roomId).orElse(null);
        validationService.validateIsRoomExist(room);

        final RoomEntry roomEntry = roomEntryRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, currentUserId)
                .orElse(null);
        validationService.validateIsUserJoined(roomEntry);

        roomEntry.leaveRoom();
        roomEntryRepository.save(roomEntry);

        //redis에 갱신
        redisSynchronizer.removeUser(roomId, currentUserId);
        
        room.getEntries().stream()
                .filter(entry -> entry.getLeftAt() == null)
                .findAny()
                .orElseGet(() -> {
                    room.setDeletedAt(LocalDateTime.now());
                    redisSynchronizer.removeRoom(roomId);
                    return null;
                });

    }

    
    /**
     * 사용자 초대
     * @param roomId 초대할 방 ID
     * @param userId 초대할 사용자 ID
     */
    @Transactional
    public InviteUserResponse inviteUserToRoom(final Long roomId, final List<Long> friendIds) {

        final Long currentUserId = SecurityUtil.getCurrentUserId();

        final Room room = roomRepository.findByIdAndDeletedAtIsNull(roomId).orElse(null);

        validationService.validateIsFriends(currentUserId, friendIds);
        validationService.validateIsUserInvitedNoOne(friendIds);
        validationService.validateIsUserInvitedItselves(currentUserId, friendIds);
        validationService.validateIsRoomExist(room);

        validationService.validateIsUserJoined(roomEntryRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, currentUserId).orElse(null));

        final List<Long> invitedUserIds = friendIds.stream().distinct().toList().stream().map(userId -> {
            
            validationService.validateIsUserNotJoined(roomEntryRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, userId).orElse(null));

            final RoomEntry roomEntry = roomEntryRepository.save(RoomEntry.builder()
                    .room(room)
                    .userId(userId)
                    .joinedAt(LocalDateTime.now())
                    .build());

            redisSynchronizer.addUser(roomId, roomEntry.getUserId());

            return roomEntry.getUserId();
        }).toList();

        

        return InviteUserResponse.builder()
                .roomId(roomId)
                .invitedUserIds(invitedUserIds)
                .build();
    }

    /**
     * 방 참가자 목록 조회
     * @param roomId 방 ID
     * @return 참가자 목록 (userId 리스트)
     */
    @Transactional(readOnly = true)
    public List<Long> getParticipantsInRoom(final Long roomId) {

        final Long currentUserId = SecurityUtil.getCurrentUserId();

        validationService.validateIsRoomExist(roomRepository.findByIdAndDeletedAtIsNull(roomId).orElse(null));
        validationService.validateIsUserJoined(roomEntryRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, currentUserId).orElse(null));

        final List<Long> userIds = roomEntryRepository.findByRoomIdAndLeftAtIsNull(roomId).stream()
                .map(RoomEntry::getUserId)
                .toList();

        redisSynchronizer.synchronizeUser(roomId, userIds);

        return userIds;
    }

    public Set<String> getRedisParticipants(final Long roomId){
        return redisSynchronizer.getRedisParticipants(roomId);
    }

    public boolean isParticipant(final Long roomId){
        
        final Long currentUserId = SecurityUtil.getCurrentUserId();

        return roomEntryRepository.existsByRoomIdAndUserIdAndLeftAtIsNull(roomId, currentUserId);

    }


}
