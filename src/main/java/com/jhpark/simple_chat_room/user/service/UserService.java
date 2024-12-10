package com.jhpark.simple_chat_room.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    public boolean isFriends(final Long userId, final List<Long> friendId) {

        return true;

    }

    
}