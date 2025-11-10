package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.EmailActivationToken;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.redis.EmailActivationTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test for EmailActivationTokenService")
public class EmailActivationTokenServiceTest {
    @InjectMocks
    EmailActivationTokenService emailActivationTokenService;

    @Mock
    private EmailActivationTokenRepository emailActivationTokenRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test scenario for isRegistrationTokenExpired")
    class IsRegistrationTokenExpired{
        @Test
        void givenToken_whenIsRegistrationTokenExpired_thenTrueAssertBody() {

        }

        @Test
        void givenToken_whenIsRegistrationTokenExpired_thenFalseAssertBody() {

        }

    }

    @Nested
    @DisplayName("Test scenario for create")
    class Create{
        @Test
        void givenUser_whenCreate_thenEmailActivationToken() {

        }

        @Test
        void givenUser_whenCreate_thenThrowBadRequestException() {

        }

    }

    @Nested
    @DisplayName("Test scenario for getUserByToken")
    class GetUserByToken{
        @Test
        void givenToken_thenGetUserByToken_thenNotFoundException(){

        }

        @Test
        void givenToken_thenGetUserByToken_thenRegistrationTokenExpiredException(){

        }


        @Test
        void givenToken_thenGetUserByToken_thenAssertBody(){

        }

    }
    @Nested
    @DisplayName("Test scenario for deleteByUserId")
    class DeleteByUserId{
        @Test
        void givenUserId_whenDeleteByUserId_thenAssertBody() {

        }
    }

}

