package com.jhpark.simple_chat_room.room.service;

import java.util.List;

public class RoomValidationService {


    public void validateUserHasPermissionToInviteOther(final Long userId, final List<Long> otherUserIds) {
        if (otherUserIds.contains(userId)) {
            throw new RuntimeException("자신을 초대할 수 없습니다.");
        }

        if (otherUserIds.size() != otherUserIds.stream().distinct().count()) {
            throw new RuntimeException("중복된 사용자가 포함되어 있습니다.");
        }


    }

    public void validateCreateRoomRequest(final List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new RuntimeException("최소 한 명 이상의 사용자를 추가해야 합니다.");
        }
    }
    
}
