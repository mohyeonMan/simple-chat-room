package com.jhpark.simple_chat_room.room.service;

import com.jhpark.simple_chat_room.room.entity.Room;
import com.jhpark.simple_chat_room.room.entity.RoomEntry;
import com.jhpark.simple_chat_room.room.repository.RoomEntryRepository;
import com.jhpark.simple_chat_room.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomEntryRepository roomEntryRepository;


        /**
     * 방 생성 및 사용자 추가
     * @param roomName 방 이름
     * @param userIds 추가할 사용자 ID 리스트 (최소 1명 이상이어야 함)
     * @return 생성된 방 정보
     */
    @Transactional
    public Room createRoom(String roomName, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("최소 한 명 이상의 사용자를 추가해야 합니다.");
        }

        // 방 생성
        Room room = Room.builder()
                .roomName(roomName)
                .createdAt(LocalDateTime.now())
                .build();
        Room savedRoom = roomRepository.save(room);

        // 사용자 추가
        for (Long userId : userIds) {
            RoomEntry entry = RoomEntry.builder()
                    .room(savedRoom)
                    .userId(userId)
                    .joinedAt(LocalDateTime.now())
                    .build();
            roomEntryRepository.save(entry);
        }

        return savedRoom;
    }

    /**
     * 방 삭제 (논리 삭제)
     * @param roomId 삭제할 방 ID
     */
    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        if (room.getDeletedAt() != null) {
            throw new IllegalStateException("이미 삭제된 방입니다.");
        }

        // 방 논리 삭제
        room.setDeletedAt(LocalDateTime.now());
        roomRepository.save(room);

        // 선택적으로 방 참가자도 논리 삭제 처리
        List<RoomEntry> entries = roomEntryRepository.findByRoomIdAndLeftAtIsNull(roomId);
        for (RoomEntry entry : entries) {
            entry.setLeftAt(LocalDateTime.now());
            roomEntryRepository.save(entry);
        }
    }

    
    /**
     * 사용자 초대
     * @param roomId 초대할 방 ID
     * @param userId 초대할 사용자 ID
     */
    @Transactional
    public void inviteUserToRoom(final Long roomId, final Long userId) {
        
        // 방 찾기
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        // 이미 초대된 사용자 확인
        boolean alreadyInvited = roomEntryRepository.existsByRoomIdAndUserIdAndLeftAtIsNull(roomId, userId);
        if (alreadyInvited) {
            throw new IllegalStateException("사용자가 이미 방에 초대되었습니다.");
        }

        // RoomEntry 생성 및 저장
        RoomEntry roomEntry = RoomEntry.builder()
                .room(room)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .build();

        roomEntryRepository.save(roomEntry);
    }

    /**
     * 방에서 사용자 제거 (논리 삭제)
     * @param roomId 방 ID
     * @param userId 제거할 사용자 ID
     */
    @Transactional
    public void removeUserFromRoom(Long roomId, Long userId) {
        // RoomEntry 찾기
        RoomEntry roomEntry = roomEntryRepository.findByRoomIdAndUserIdAndLeftAtIsNull(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 방에 참가 중이지 않습니다."));

        // leftAt 업데이트 (논리 삭제)
        roomEntry.setLeftAt(LocalDateTime.now());
        roomEntryRepository.save(roomEntry);
    }

    /**
     * 방 참가자 목록 조회
     * @param roomId 방 ID
     * @return 참가자 목록 (userId 리스트)
     */
    @Transactional(readOnly = true)
    public List<Long> getParticipantsInRoom(Long roomId) {
        return roomEntryRepository.findUserIdsByRoomIdAndLeftAtIsNull(roomId);
    }
}
