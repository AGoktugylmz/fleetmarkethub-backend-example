package com.cosmosboard.fmh.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@RedisHash("email_activation_tokens")
public class EmailActivationToken extends BaseEntity {
    @Id
    @Indexed
    private String token;

    @Indexed
    private String userId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long timeToLive = TimeUnit.MINUTES.toMillis(30);
}
