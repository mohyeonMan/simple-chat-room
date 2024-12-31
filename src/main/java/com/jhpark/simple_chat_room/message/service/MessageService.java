package com.jhpark.simple_chat_room.message.service;

import com.jhpark.simple_chat_room.message.entity.Message;
import com.jhpark.simple_chat_room.message.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional
    public void deleteMessage(Long messageId) {
        final Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("MESSAGE NOT FOUND : messageId="+messageId));
        message.deleteMessage();
    }

    public Page<Message> getMessagesByRoomId(Long roomId, Pageable pageable) {
        return messageRepository.findByRoomIdAndDeletedAtIsNull(roomId, pageable);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("MESSAGE NOT FOUND : messageId="+messageId));
    }
}
