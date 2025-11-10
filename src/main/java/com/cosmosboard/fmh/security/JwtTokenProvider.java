package com.cosmosboard.fmh.security;

import com.cosmosboard.fmh.entity.JwtToken;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.repository.redis.JwtTokenRepository;
import com.cosmosboard.fmh.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final int SECONDS = 1000;

    private final UserService userService;

    private final JwtTokenRepository jwtTokenRepository;

    @Value("${app.secret}") private String appSecret;

    @Value("${app.jwt.token.expires-in}") private Long jwtExpiresIn;

    @Value("${app.jwt.refresh-token.expires-in}") private Long refreshExpiresIn;

    public String generateTokenByUserId(String id, Long expires) {
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(getExpireDate(expires))
                .signWith(SignatureAlgorithm.HS512, appSecret)
                .compact();
    }

    public String generateJwt(String id) {
        String token = generateTokenByUserId(id, jwtExpiresIn);
        log.trace("Jwt Token is added to the local cache for userID: {}, ttl: {}", id, jwtExpiresIn);
        jwtTokenRepository.save(JwtToken.builder().token(token).userId(id).timeToLive(jwtExpiresIn).build());
        return token;
    }

    public String generateRefresh(String id) {
        String token = generateTokenByUserId(id, refreshExpiresIn);
        log.trace("Refresh Token is added to the local cache for userID: {}, ttl: {}", id, refreshExpiresIn);
        jwtTokenRepository.save(JwtToken.builder().token(token).userId(id).timeToLive(refreshExpiresIn).build());
        return token;
    }

    public JwtUserDetails getPrincipal(Authentication authentication) {
        return userService.getPrincipal(authentication);
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token).getBody();

        return claims.getSubject();
    }

    public User getUserFromToken(String token) {
        try {
            return userService.findOneById(getUserIdFromToken(token));
        } catch (NotFoundException e) {
            return null;
        }
    }

    /**
     * Boolean result of whether token is valid or not
     *
     * @param token String token
     * @return true or false
     */
    public boolean validateToken(String token) {
        if (jwtTokenRepository.findById(token).isEmpty()) {
            log.error("Token is not found in redis, returning false.");
            return false;
        }
        parseToken(token);
        return !isTokenExpired(token);
    }

    public boolean validateToken(String token, HttpServletRequest httpServletRequest) {
        try {
            boolean isTokenValid = validateToken(token);
            if (!isTokenValid) {
                log.error("[JWT] Token could not found in local cache");
                httpServletRequest.setAttribute("notfound", "Token is not found in redis");
            }
            return isTokenValid;
        } catch (UnsupportedJwtException e) {
            log.error("[JWT] Unsupported JWT token!");
            httpServletRequest.setAttribute("unsupported", "Unsupported JWT token!");
        } catch (SignatureException | MalformedJwtException e) {
            log.error("[JWT] Invalid JWT token!");
            httpServletRequest.setAttribute("invalid", "Invalid JWT token!");
        } catch (ExpiredJwtException e) {
            log.error("[JWT] Expired JWT token!");
            httpServletRequest.setAttribute("expired", "Expired JWT token!");
        } catch (IllegalArgumentException e) {
            log.error("[JWT] Jwt claims string is empty");
            httpServletRequest.setAttribute("illegal", "JWT claims string is empty.");
        }
        return false;
    }

    public Long getJwtExpiresIn() {
        return this.jwtExpiresIn;
    }

    /**
     * Parsing token
     *
     * @param token String jwt token to parse
     * @return Jws object
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parser().setSigningKey(appSecret).parseClaimsJws(token);
    }

    /**
     * Check token is expired or not,
     *
     * @param token String jwt token to get expiration date
     * @return True or False
     */
    private boolean isTokenExpired(String token) {
        return parseToken(token).getBody().getExpiration().before(new Date());
    }

    /**
     * Get expire date
     *
     * @return Date object
     */
    private Date getExpireDate(Long expires) {
        return new Date(new Date().getTime() + expires * SECONDS);
    }

    /**
     * When user logging out or create a new token with refresh token, all the tokens should be removed from cache for security
     *
     * @param userId String user identifier which is added to the key as value
     */
    public void markLogoutEventForToken(String userId) {
        log.debug("Logged out. Jwt and Refresh tokens for user {} removed in redis", userId);
        List<JwtToken> allByUserId = jwtTokenRepository.findAllByUserId(userId);
        for (JwtToken jwtToken : allByUserId) {
            log.info("[JwtToken]: UserId:{}, Token:{}, TimeToLive:{}",
                    jwtToken.getUserId(), jwtToken.getToken(), jwtToken.getTimeToLive());
        }
        jwtTokenRepository.deleteAll(allByUserId);
    }
}