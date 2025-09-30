package com.hotsix.server.redis;

import com.hotsix.server.auth.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("Redis refreshtoken 저장,조회 테스트")
    void redis_refreshToken_test() {
        String key = "refreshToken:1";
        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .token("abc.def.ghi")
                .expiry(System.currentTimeMillis() + 3600000) // 1시간 후
                .build();

        redisTemplate.opsForValue().set(key, token);

        Object value = redisTemplate.opsForValue().get(key);
        assertThat(value).isInstanceOf(RefreshToken.class);

        RefreshToken retrieved = (RefreshToken) value;
        assertThat(retrieved.getUserId()).isEqualTo(1L);
        assertThat(retrieved.getToken()).isEqualTo("abc.def.ghi");

        System.out.println("Redis 저장된 RefreshToken 확인: " + retrieved);

        redisTemplate.delete(key);
    }
}
