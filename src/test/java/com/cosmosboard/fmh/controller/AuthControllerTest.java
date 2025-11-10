package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.auth.LoginRequest;
import com.cosmosboard.fmh.dto.request.auth.RefreshRequest;
import com.cosmosboard.fmh.dto.request.auth.RegisterRequest;
import com.cosmosboard.fmh.dto.request.user.ChangePasswordRequest;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.auth.TokenResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.AuthService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for AuthController")
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    private final User user = Factory.createUser();

    private final RegisterRequest registerRequest = Factory.createRegisterRequest();

    private MultipartFile avatar;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        avatar = mock(MultipartFile.class);
        when(objectMapper.readValue(anyString(), eq(RegisterRequest.class))).thenReturn(registerRequest);
    }

//    @Nested
//    @DisplayName("Test class for login scenarios")
//    class LoginTest {
//        private final LoginRequest loginRequest = Factory.createLoginRequest();
//        private final TokenResponse tokenResponse = TokenResponse.builder().build();
//
//        @Test
//        @DisplayName("Happy Path")
//        void givenLoginRequest_whenLogin_thenAssertBody() {
//            // Given
//            when(authService.login(loginRequest.getEmail(), loginRequest.getPassword()))
//                    .thenReturn(tokenResponse);
//            when(userService.getUser()).thenReturn(user);
//            // When
//            TokenResponse response = authController.login(loginRequest);
//            // Then
//            assertNotNull(response);
//            assertEquals(tokenResponse, response);
//        }
//    }
//
//    @Nested
//    @DisplayName("Test class for register scenarios")
//    class RegisterTest {
//
//        @Test
//        void givenRegisterRequest_whenPasswordNotValid_thenThrowsBadRequestException () throws JsonProcessingException {
//            // Given
//            String requestJson = "{\"email\":\"test@example.com\", \"password\":\"invalid\"}";
//            RegisterRequest registerRequest = new RegisterRequest();
//            registerRequest.setEmail("test@example.com");
//            registerRequest.setPassword("invalid");
//
//            when(objectMapper.readValue(requestJson, RegisterRequest.class)).thenReturn(registerRequest);
//            when(AppUtil.isPasswordValid("invalid")).thenReturn(List.of("Password must contain at least one uppercase letter."));
//
//            when(messageSourceService.get("bad_password_format")).thenReturn("Password does not meet requirements");
//
//            // When & Then
//            BadRequestException exception = assertThrows(BadRequestException.class, () -> authController.register(requestJson, null));
//            assertEquals("Password must contain at least one uppercase letter.", exception.getMessage());
//        }
//
//        @Test
//        void givenRegisterRequest_whenRegisterWithEmailExist_thenThrowsBadRequestException () throws JsonProcessingException {
//            // Given
//            User user = Factory.createUser();
//
//            String requestJson = "{\"email\":\"test@example.com\"}";
//            when(objectMapper.readValue(requestJson, RegisterRequest.class)).thenReturn(registerRequest);
//
//            when(userService.createEmployee(registerRequest, avatar)).thenReturn(user);
//            when(userService.existsByEmail("test@example.com")).thenReturn(false);
//
//            // When
//            UserResponse response = authController.register(requestJson, avatar);
//
//            // Then
//            assertNotNull(response);
//            assertEquals("test@example.com", response.getEmail());
//        }
//
//        @Test
//        @DisplayName("Happy Path")
//        void givenRegisterRequest_whenRegister_thenAssertBody() throws JsonProcessingException {
//            // Given
////            doReturn(user).when(userService).register(registerRequest);
//            // When
//            UserResponse response = authController.register(objectMapper.writeValueAsString(registerRequest),
//                    null);
//            // Then
////            assertNotNull(response);
////            assertNotNull(response.getBody());
////            assertEquals(HttpStatus.CREATED, response.getStatusCode());
////            assertEquals(user.getId(), response.getBody().getId());
////            assertEquals(user.getEmail(), response.getBody().getEmail());
////            assertEquals(user.getName(), response.getBody().getName());
////            assertEquals(user.getLastName(), response.getBody().getLastName());
////            assertEquals(user.getRoles().size(), response.getBody().getRoles().size());
////            assertTrue(response.getBody().getRoles().stream().findFirst().isPresent());
////            assertTrue(user.getRoles().stream().findFirst().isPresent());
////            assertEquals(user.getRoles().stream().findFirst().get().getName().toString(),
////                    response.getBody().getRoles().stream().findFirst().get());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test class for resend activation e-mail scenarios")
//    class ResendActivationEmailTest {
//        @Test
//        @DisplayName("Happy Path")
//        void givenResendActivationEmailRequest_whenResendActivation_thenAssertBody() {
//            // Given
//            Mockito.doReturn("Activation e-mail sent!").when(messageSourceService)
//                    .get("activation_email_sent");
//            // When
//            JsonNode response = authController.resendEmailActivation("lorem@ipsum.com");
//            // Then
//            assertNotNull(response);
////            assertNotNull(response.getBody());
////            assertEquals("Activation e-mail sent!", response.getBody().getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test class for activation scenarios")
//    class ActivationTest {
//        @Test
//        @DisplayName("Happy Path")
//        void givenToken_whenActivateEmail_thenAssertBody() {
//            // Given
//            Mockito.doReturn("Your e-mail activated successfully!").when(messageSourceService)
//                    .get("your_email_activated");
//            // When
//            SuccessResponse response = authController.activateEmail("token");
//            // Then
//            assertNotNull(response);
//            assertEquals("Your e-mail activated successfully!", response.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test class for refresh scenarios")
//    class RefreshTest {
//        private final RefreshRequest refreshRequest = Factory.createRefreshRequest();
//        private final TokenResponse tokenResponse = TokenResponse.builder().build();
//
//        @Test
//        @DisplayName("Happy Path")
//        void givenRefreshRequest_whenRefresh_thenAssertBody() {
//            // Given
//            doReturn(tokenResponse).when(authService).refresh(refreshRequest.getToken());
//            // When
//            TokenResponse response = authController.refresh(refreshRequest);
//            // Then
//            assertNotNull(response);
//            assertEquals(tokenResponse, response);
//        }
//    }
//
////    @Nested
////    @DisplayName("Test class for reset password scenarios")
////    class ResetPasswordTest {
////        @Test
////        void givenResetPasswordRequest_whenResetPassword_thenAssertResponse() {
////            // Given
////            String expectedMessage = "Password reset link sent";
////            when(messageSourceService.get("password_reset_link_sent")).thenReturn(expectedMessage);
////
////            // When
////            ResponseEntity<SuccessResponse> response = authController.resetPassword("lorem@ipsum.com");
////
////            // Then
////            assertNotNull(response);
////            assertNotNull(response.getBody());
////            assertEquals(expectedMessage, response.getBody().getMessage());
////            verify(messageSourceService).get("password_reset_link_sent");
////            verifyNoMoreInteractions(userService, messageSourceService);
////        }
////    }
//
//    @Nested
//    @DisplayName("Test class for change password scenarios")
//    class ChangePasswordTest {
//        @Test
//        void givenChangePasswordRequestAndToken_whenChangePassword_thenAssertResponse() {
//            // Given
//            String token = "sampleToken";
//            String expectedMessage = "Password changed successfully";
//            ChangePasswordRequest changePasswordRequest = Factory.createChangePasswordRequest();
//            when(messageSourceService.get("password_changed_success")).thenReturn(expectedMessage);
//
//            // When
//            SuccessResponse response = authController.changePassword(changePasswordRequest, token);
//
//            // Then
//            assertNotNull(response);
//            assertEquals(expectedMessage, response.getMessage());
//            verify(userService).changePassword(changePasswordRequest, token);
//            verify(messageSourceService).get("password_changed_success");
//            verifyNoMoreInteractions(userService, messageSourceService);
//        }
//    }
}
