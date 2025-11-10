package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.response.auth.TokenResponse;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.RefreshTokenExpiredException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.security.JwtTokenProvider;
import com.cosmosboard.fmh.security.JwtUserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for AuthService")
public class AuthServiceTest {
    @InjectMocks
    AuthService authService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock UserService userService;
    @Mock MessageSourceService messageSourceService;


    private final User user = Factory.createUser();
    private final JwtUserDetails jwtUserDetails =  JwtUserDetails.create(user);

    @Nested
    @DisplayName("Test class for login scenarios")
    class LoginTest {
        @Mock
        Authentication authentication;

        @Test
        public void givenEmailAndPassword_whenLogin_thenThrowBadRequestException() {
            // Given
            String email = "test@example.com";
            String password = "password123";

            User nonActivatedUser = Factory.createUser();
            nonActivatedUser.setEmailActivatedAt(null);

            when(userService.findOneByEmail(email)).thenReturn(nonActivatedUser);
            when(messageSourceService.get("email_not_activated")).thenReturn("Email not activated");


            // When & Then
            assertThrows(BadRequestException.class, () -> authService.login(email, password));
        }

        @Test
        @DisplayName("Happy Path")
        void givenEmailAndPassword_whenLogin_thenAssertBody() {
            // Given
            doReturn(authentication).when(authenticationManager)
                    .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
            doReturn(jwtUserDetails).when(jwtTokenProvider).getPrincipal(authentication);

            User activatedUser = Factory.createUser();
            activatedUser.setEmailActivatedAt(LocalDateTime.now());
            when(userService.findOneByEmail("email")).thenReturn(activatedUser);

            when(jwtTokenProvider.generateJwt(jwtUserDetails.getId()))
                    .thenReturn("Jwt");
            when(jwtTokenProvider.generateRefresh(jwtUserDetails.getId()))
                    .thenReturn("Token");
            when(jwtTokenProvider.getJwtExpiresIn())
                    .thenReturn(5L);

            // When
            TokenResponse response = authService.login("email", "pass");

            // Then
            assertNotNull(response);
            assertEquals("Jwt", response.getToken());
            assertEquals("Token", response.getRefreshToken());
            assertEquals(5L, response.getExpiresIn());
        }
    }

    @Nested
    @DisplayName("Test class for refresh token scenarios")
    class RefreshTest {
        private final User user = Factory.createUser();

        @Test
        @DisplayName("RefreshTokenExpiredException is expected when token not exist")
        void givenRefreshToken_whenRefreshWithNotExistToken_thenThrowServerException() {
            // Given
            doReturn(false).when(jwtTokenProvider).validateToken("token");
            // When
            Executable closureToTest = () -> authService.refresh("token");
            // Then
            Assertions.assertThrows(RefreshTokenExpiredException.class, closureToTest);
        }

        @Test
        @DisplayName("Happy Path")
        void givenRefreshToken_whenLogin_thenAssertBody() {
            // Given
            doReturn(true).when(jwtTokenProvider).validateToken("token");
            doReturn(user).when(jwtTokenProvider).getUserFromToken("token");
            doReturn("newToken").when(jwtTokenProvider).generateJwt(user.getId());
            doReturn("newRefresh").when(jwtTokenProvider).generateRefresh(user.getId());
            doReturn(1L).when(jwtTokenProvider).getJwtExpiresIn();
            // When
            TokenResponse response = authService.refresh("token");
            // Then
            assertNotNull(response);
            assertEquals("newToken", response.getToken());
            assertEquals("newRefresh", response.getRefreshToken());
            assertEquals(1L, response.getExpiresIn());
        }
    }
}
