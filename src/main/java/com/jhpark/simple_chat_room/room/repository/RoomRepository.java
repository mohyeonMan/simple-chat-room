package com.jhpark.simple_chat_room.room.repository;

import com.jhpark.simple_chat_room.room.entity.Room;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByIdAndDeletedAtIsNull(final Long roomId);

}
