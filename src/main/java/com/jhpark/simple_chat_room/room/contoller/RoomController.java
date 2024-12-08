package com.jhpark.simple_chat_room.room.contoller;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.jhpark.simple_chat_room.room.dto.CreateRoomRequest;
import com.jhpark.simple_chat_room.room.service.RoomService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController("/api/room")
@RequiredArgsConstructor
public class RoomController {
    
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Void> createRoom(@RequestBody CreateRoomRequest request) {
        // roomService.createRoom(request);
        
        return ResponseEntity.ok().build();
    }

}
