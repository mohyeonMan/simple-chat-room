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
            throw new RuntimeException("YOU CANNOT INVITE YOURSELF.");
        }
    }

    public void validateIsUserInvitedNoOne(final List<Long> otherUserIds) {
        if (otherUserIds == null || otherUserIds.isEmpty()) {
            throw new RuntimeException("YOU MUST INVITE AT LEAST ONE USER.");
        }
    }

    public void validateIsFriends(final Long userId, final List<Long> otherUserIds) {
        if (!userService.isFriends(userId, otherUserIds)) {
            throw new RuntimeException("YOU CANNOT INVITE USERS WHO ARE NOT YOUR FRIENDS.");
        }
    }

    public void validateIsRoomExist(final Room room) {
        if (room == null || room.getDeletedAt() != null) {
            throw new RuntimeException("ROOM DOES NOT EXIST.");
        }
    }

    public void validateIsUserJoined(final RoomEntry roomEntry) {
        if (roomEntry == null || roomEntry.getLeftAt() != null) {
            throw new RuntimeException("USER HAS NOT JOINED THE ROOM.");
        }
    }

    public void validateIsUsersJoined(final List<RoomEntry> roomEntries) {
        roomEntries.forEach(roomEntry -> validateIsUserJoined(roomEntry));
    }

    public void validateIsUserNotJoined(final RoomEntry roomEntry) {
        if (roomEntry != null && roomEntry.getLeftAt() == null) {
            throw new RuntimeException("USER HAS ALREADY JOINED THE ROOM.");
        }
    }

    public void validateIsUsersNotJoined(final List<RoomEntry> roomEntries) {
        roomEntries.forEach(roomEntry -> validateIsUserNotJoined(roomEntry));
    }
}
