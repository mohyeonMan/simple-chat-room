package com.jhpark.simple_chat_room.room.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InviteUserResponse {
    
    private Long roomId;
    private List<Long> invitedUserIds;

}
