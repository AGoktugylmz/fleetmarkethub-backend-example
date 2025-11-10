package com.cosmosboard.fmh.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import java.util.concurrent.TimeUnit;

@Builder
@Getter
@Setter
@RedisHash(value = "userInvite")
@ToString
public class UserInvite {
    @Id
    private String id;

    @Indexed
    private String userFrom;

    @Indexed
    private String userTo;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long timeToLive = 30L;
}