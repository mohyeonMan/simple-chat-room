package com.jhpark.simple_chat_room.message.service;

import com.jhpark.simple_chat_room.message.entity.Message;
import com.jhpark.simple_chat_room.message.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional
    public void deleteMessage(final Long messageId) {
        final Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("MESSAGE NOT FOUND : messageId="+messageId));
        message.deleteMessage();
    }

    public Page<Message> getMessagesByRoomId(final Long roomId, final Pageable pageable) {
        return messageRepository.findByRoomIdAndDeletedAtIsNull(roomId, pageable);
    }

    public Message getMessageById(final Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("MESSAGE NOT FOUND : messageId="+messageId));
    }
}
