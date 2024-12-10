package com.jhpark.simple_chat_room.room.contoller;

import com.jhpark.simple_chat_room.room.dto.request.CreateRoomRequest;
import com.jhpark.simple_chat_room.room.dto.request.InviteUserRequest;
import com.jhpark.simple_chat_room.room.dto.response.CreateRoomResponse;
import com.jhpark.simple_chat_room.room.dto.response.InviteUserResponse;
import com.jhpark.simple_chat_room.room.service.RoomService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * 방 생성
     * @param request 방 생성 요청 (방 이름, 사용자 리스트)
     * @return 생성된 방 정보
     */
    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(request.getRoomName(), request.getUserIds());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 방 나가기
     * @param roomId 방 ID
     * @return 성공 여부
     */
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId) {
        roomService.leaveRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 초대
     * @param roomId 방 ID
     * @param request 초대할 사용자 리스트
     * @return 초대 성공 여부
     */
    @PostMapping("/{roomId}/invite")
    public ResponseEntity<InviteUserResponse> inviteUserToRoom(
            @PathVariable Long roomId,
            @RequestBody InviteUserRequest request) {
        InviteUserResponse response = roomService.inviteUserToRoom(roomId, request.getUserIds());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 방 참가자 목록 조회
     * @param roomId 방 ID
     * @return 참가자 ID 리스트
     */
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<Long>> getParticipantsInRoom(@PathVariable Long roomId) {
        List<Long> participants = roomService.getParticipantsInRoom(roomId);
        return ResponseEntity.ok().body(participants);
    }
}
