package com.jhpark.simple_chat_room.room.repository;

import com.jhpark.simple_chat_room.room.entity.RoomEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomEntryRepository extends JpaRepository<RoomEntry, Long> {

    // 특정 방에 특정 사용자가 존재하는지 확인
    boolean existsByRoomIdAndUserIdAndLeftAtIsNull(Long roomId, Long userId);

    // 특정 방에 특정 사용자를 찾기
    Optional<RoomEntry> findByRoomIdAndUserIdAndLeftAtIsNull(Long roomId, Long userId);

    // 특정 방에 참가 중인 사용자 ID 목록
    List<Long> findUserIdByRoomIdAndLeftAtIsNull(Long roomId);

    // 특정 방에 참가 중인 사용자 목록
    List<RoomEntry> findByRoomIdAndLeftAtIsNull(Long roomId);
}
