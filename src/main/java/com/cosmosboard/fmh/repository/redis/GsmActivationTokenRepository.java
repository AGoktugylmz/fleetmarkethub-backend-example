package com.cosmosboard.fmh.repository.redis;

import com.cosmosboard.fmh.entity.GsmActivationToken;
import org.springframework.data.repository.CrudRepository;

public interface GsmActivationTokenRepository extends CrudRepository<GsmActivationToken, String> {
    GsmActivationToken findByUserId(String userId);

    GsmActivationToken findByTokenAndUserId(String token, String userId);
}
