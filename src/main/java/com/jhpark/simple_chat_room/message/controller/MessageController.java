package com.jhpark.simple_chat_room.message.controller;

import com.jhpark.simple_chat_room.message.entity.Message;
import com.jhpark.simple_chat_room.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Page<Message>> getMessagesByRoom(
            @PathVariable Long roomId,
            Pageable pageable) {
        Page<Message> messages = messageService.getMessagesByRoomId(roomId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }
}
