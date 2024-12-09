package com.jhpark.simple_chat_room.room.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoomResponse {

    private Long roomId;
    private String roomName;
    
}
