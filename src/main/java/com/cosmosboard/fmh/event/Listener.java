package com.cosmosboard.fmh.event;

import com.cosmosboard.fmh.entity.EmailActivationToken;
import com.cosmosboard.fmh.entity.GsmActivationToken;
import com.cosmosboard.fmh.entity.JwtToken;
import com.cosmosboard.fmh.entity.UserInvite;
import com.cosmosboard.fmh.service.EmailService;
import com.cosmosboard.fmh.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Listener {
    private final EmailService emailService;

    private final SmsService smsService;

    @EventListener(UserEmailActivationSendEvent.class)
    public void onUserEmailActivationSendEvent(UserEmailActivationSendEvent event) {
        log.info("[User email activation email send event listener] {} - {}", event.getUser().getEmail(), event.getUser().getId());
        emailService.sendUserEmailActivation(event.getUser());
    }

    @EventListener(UserGsmActivationSendEvent.class)
    public void onUserGsmActivationSendEvent(UserGsmActivationSendEvent event) {
        log.info("[User gsm activation gsm send event listener] {} - {}", event.getUser().getGsm(), event.getUser().getId());
        smsService.sendUserGsmActivation(event.getUser());
    }

    @EventListener(UserInvitationSendEvent.class)
    public void onUserInvitationSendEvent(UserInvitationSendEvent event) {
        log.info("[User invitation email send event listener] {} - {}",
                event.getUser().getEmail(), event.getUser().getId());
        emailService.sendUserInvitation(event.getUser(), event.getUserInvite());
    }

    // TODO: make this switch pattern for Java21
    @EventListener(RedisKeyExpiredEvent.class)
    public void onRedisKeyExpiredEvent(RedisKeyExpiredEvent<Object> event) {
        Object value = event.getValue();
        if (value == null) {
            log.warn("Value is null, returning...");
            return;
        }
        if (value.getClass() == UserInvite.class) {
            UserInvite userInvite = (UserInvite) value;
            log.info("User invitation from: {}, to: {} is expired.", userInvite.getUserFrom(), userInvite.getUserTo());
            return;
        }
        if (value.getClass() == JwtToken.class) {
            JwtToken jwtToken = (JwtToken) value;
            log.info("JwtToken of user: {} is expired.", jwtToken.getUserId());
            return;
        }
        if (value.getClass() == EmailActivationToken.class) {
            EmailActivationToken token = (EmailActivationToken) value;
            log.info("EmailActivationToken of user: {} is expired.", token.getUserId());
            return;
        }
        if (value.getClass() == GsmActivationToken.class) {
            GsmActivationToken token = (GsmActivationToken) value;
            log.info("GsmActivationToken of user: {} is expired.", token.getUserId());
            return;
        }
    }
}
