package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for SmsService")
public class SmsServiceTest {

    @Mock
    private User user;

    @InjectMocks
    private SmsService smsService;

    @Value("${app.sms-from}")
    private String smsFrom;

    @BeforeEach
    void setUp() {
        when(user.getId()).thenReturn("12345");
        when(user.getGsm()).thenReturn("+123456789");
        when(user.getGsmActivationToken()).thenReturn("token123");
    }

    @Nested
    @DisplayName("Test for sendUserGsmActivation scenarios")
    class SendUserGsmActivationTest {
        @Test
        void givenUser_whenSendUserGsmActivation_thenLogsArePrinted() {
            // When
            smsService.sendUserGsmActivation(user);

            // Then
            verify(user, times(1)).getId();
            verify(user, times(3)).getGsm();
            verify(user, times(1)).getGsmActivationToken();
        }
    }
}