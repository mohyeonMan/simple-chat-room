-- synchronize_users.lua
-- KEYS[1]: 방의 Key (예: room:{roomId}:participants)
-- ARGV: 사용자 ID 목록

local roomKey = KEYS[1]   -- 방 Key
local users = ARGV        -- 사용자 목록

-- 기존 데이터를 삭제
redis.call('del', roomKey)

-- 사용자 목록 추가
for i, userId in ipairs(users) do
    redis.call('sadd', roomKey, userId)
end

-- 결과 반환 (방의 모든 사용자)
return redis.call('smembers', roomKey)
