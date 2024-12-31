package com.jhpark.simple_chat_room.message.repository;

import com.jhpark.simple_chat_room.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByRoomIdAndDeletedAtIsNull(Long roomId, Pageable pageable);
}
