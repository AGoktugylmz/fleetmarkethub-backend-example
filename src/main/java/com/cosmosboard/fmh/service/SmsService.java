package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {
    private final String from;

    /**
     * Initializes the SMS service with the sender information.
     *
     * @param from the sender ID or phone number for SMS
     */
    public SmsService(@Value("${app.sms-from}") String from) {
        this.from = from;
    }

    /**
     *  It performs a process to send a GSM activation message to the user.
     *
     * @param user User
     */
    public void sendUserGsmActivation(User user) {
        log.info(String.format("[SmsService] Sending GSM activation: %s - %s", from, user.getGsm()));
        log.info(String.format("[SmsService] Sending GSM activation token: %s - %s - %s", user.getId(), user.getGsm(),
                user.getGsmActivationToken()));
        // TODO: SMS provider integration
        log.info(String.format("[SmsService] Sent GSM activation: %s - %s", from, user.getGsm()));
    }
}
