package com.jhpark.simple_chat_room.redis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.RedisScriptingCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean save(final String key, final Set<String> value){

        return false;
    }

    public boolean save(final String key, final String value) {
        log.info("REDIS ADD : key={}, value={}", key, value);
        Long addedCount = redisTemplate.opsForSet().add(key, value);

        return Optional.ofNullable(addedCount).orElse(0L) > 0;
    }


    public boolean delete(final String key, final String value) {
        log.info("REDIS REMOVE VALUE : key={}, value={}", key, value);
        final Long removedCount = redisTemplate.opsForSet().remove(key, value);
        return Optional.ofNullable(removedCount).orElse(0L) > 0;
    }

    public boolean deleteKey(final String key) {
        log.info("REDIS REMOVE KEY : key={}", key);
        return redisTemplate.delete(key);
    }

    public Set<String> get(final String key) {
        log.info("REDIS GET : key={}", key);
        final Set<String> members = redisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return Set.of();
        }
        return members;
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

    public Long getSetSize(final String key){
        if(isExistKey(key)){
            Long size = redisTemplate.opsForSet().size(key);
            log.info("REDIS SET SIZE : key={}, size={}",key, size);
            return size;
        }
        log.info("REDIS SET SIZE : NOT EXISTS KEY : key={} ",key);
        return 0L;
    }

    public <T> T runLuaScript(final RedisScript<T> script, final List<String> keys, List<String> args) {
        final T result = redisTemplate.execute(script, keys, args.toArray());
        return result;
    }
    

    
}

