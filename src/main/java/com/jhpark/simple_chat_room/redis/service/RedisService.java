package com.jhpark.simple_chat_room.redis.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean save(final String key, final String value) {
        log.info("REDIS ADD : key={}, value={}", key, value);
        Long addedCount = redisTemplate.opsForSet().add(key, value);
        return Optional.ofNullable(addedCount).orElse(0L) > 0;
    }

    public boolean delete(final String key, final String value) {
        log.info("REDIS REMOVE : key={}, value={}", key, value);
        final Long removedCount = redisTemplate.opsForSet().remove(key, value);
        return Optional.ofNullable(removedCount).orElse(0L) > 0;
    }

    public Set<String> get(final String key) {
        log.info("REDIS GET : key={}", key);
        final Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return Set.of();
        }
        return members.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public boolean isExistKey(final String key) {
        final boolean exists = Optional.ofNullable(redisTemplate.hasKey(key)).orElse(false);
        log.info("REDIS EXISTS : key={}, exists={}", key, exists);
        return exists;
    }

    public boolean isMember(final String key, final String value) {
        final boolean exists = Optional.ofNullable(
            redisTemplate.opsForSet().isMember(key, value)).orElse(false);
        log.info("REDIS ISMEMBER : key={}, value={}, exists={}", key, value, exists);
        return exists;
    }
}

