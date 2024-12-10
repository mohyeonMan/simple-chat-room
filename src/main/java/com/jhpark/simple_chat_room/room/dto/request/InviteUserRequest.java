package com.jhpark.simple_chat_room.room.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class InviteUserRequest {
    private List<Long> userIds;
}
