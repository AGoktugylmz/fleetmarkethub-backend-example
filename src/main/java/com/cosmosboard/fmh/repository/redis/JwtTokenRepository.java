package com.cosmosboard.fmh.repository.redis;

import com.cosmosboard.fmh.entity.JwtToken;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface JwtTokenRepository extends CrudRepository<JwtToken, String> {
    List<JwtToken> findAllByUserId(String userId);
}