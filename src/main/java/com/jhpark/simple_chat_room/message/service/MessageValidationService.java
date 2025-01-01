package com.jhpark.simple_chat_room.message.service;

import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_room.message.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageValidationService {

    private final MessageRepository messageRepository;

    public void validateIsMessageExists(final Long messageId){
        if(isMessageExists(messageId)){
            throw new RuntimeException("MESSAGE NOT FOUND : messageId="+messageId);
        }
    }

    public boolean isMessageExists(final Long messageId){
        return true;
    }

    public boolean isMessageSender(final Long userId, final Long messageId){
        return true;
    }
    

    
    
}
