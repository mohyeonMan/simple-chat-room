package com.jhpark.simple_chat_room.room.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_room.room.entity.Room;
import com.jhpark.simple_chat_room.room.entity.RoomEntry;
import com.jhpark.simple_chat_room.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomValidationService {

    private final UserService userService;

    public void validateCreateRoomRequest(final Long userid, List<Long> otherIds) {

        validateIsUserInvitedNoOne(otherIds);
        validateIsUserInvitedItselves(userid, otherIds);
        validateIsFriends(userid, otherIds);

    }

    public void validateIsUserInvitedItselves(final Long userId, final List<Long> otherUserIds) {
        if (otherUserIds.contains(userId)) {
            throw new RuntimeException("자신을 초대할 수 없습니다.");
        }
    }

    public void validateIsUserInvitedNoOne(final List<Long> otherUserIds) {
        if (otherUserIds == null || otherUserIds.isEmpty()) {
            throw new RuntimeException("최소 한 명 이상의 사용자를 추가해야 합니다.");
        }
    }

    public void validateIsFriends(final Long userId, final List<Long> otherUserIds) {
        if (!userService.isFriends(userId, otherUserIds)) {
            throw new RuntimeException("친구가 아닌 사용자를 초대할 수 없습니다.");
        }
    }

    public void validateIsRoomExist(final Room room){
        if(room == null || room.getDeletedAt() != null){
            throw new RuntimeException("존재하지 않는 방입니다.");
        }
    }

    public void validateIsUserJoined(final RoomEntry roomEntry){
        if(roomEntry == null || roomEntry.getLeftAt() != null){
            throw new RuntimeException("방에 참여하지 않은 사용자입니다.");
        }
    }

    public void validateIsUsersJoined(final List<RoomEntry> roomEntries){
        roomEntries.forEach(roomEntry -> validateIsUserJoined(roomEntry));
    }

    public void validateIsUserNotJoined(final RoomEntry roomEntry){
        if(roomEntry != null && roomEntry.getLeftAt() == null){
            throw new RuntimeException("이미 방에 참여한 사용자입니다.");
        }
    }

    public void validateIsUsersNotJoined(final List<RoomEntry> roomEntries){
        roomEntries.forEach(roomEntry -> validateIsUserNotJoined(roomEntry));
    }
    
}
