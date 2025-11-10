package com.cosmosboard.fmh.repository.redis;

import com.cosmosboard.fmh.entity.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserId(String userId);
}
