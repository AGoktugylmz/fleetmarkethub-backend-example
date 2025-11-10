package com.cosmosboard.fmh.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import java.util.concurrent.TimeUnit;

@Builder
@Getter
@Setter
@RedisHash(value = "jwtTokens")
public class JwtToken {
    @Id
    private String token;

    @Indexed
    private String userId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long timeToLive;
}