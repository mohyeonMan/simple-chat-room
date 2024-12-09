package com.jhpark.simple_chat_room.room.dto.request;


import java.util.List;

import lombok.Data;

@Data
public class CreateRoomRequest {
    
    private String roomName;
    private List<Long> userIds;
    
}
