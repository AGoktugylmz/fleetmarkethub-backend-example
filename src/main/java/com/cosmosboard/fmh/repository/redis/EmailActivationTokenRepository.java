package com.cosmosboard.fmh.repository.redis;

import com.cosmosboard.fmh.entity.EmailActivationToken;
import org.springframework.data.repository.CrudRepository;

public interface EmailActivationTokenRepository extends CrudRepository<EmailActivationToken, String> {
    EmailActivationToken findByUserId(String userId);

    EmailActivationToken findByToken(String token);
}
