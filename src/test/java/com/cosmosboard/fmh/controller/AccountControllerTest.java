package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.user.ActivateGsmRequest;
import com.cosmosboard.fmh.dto.request.user.UpdatePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateProfileRequest;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.RandomStringGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import static com.cosmosboard.fmh.util.AppConstants.GSM_ACTIVATION_TOKEN_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for AuthController")
public class AccountControllerTest {
    @InjectMocks
    private AccountController accountController;

    @Mock
    private UserService userService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Test class for me scenarios")
    class MeTest {
        @Test
        void givenUserResponse_whenMe_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            doReturn(user).when(userService).getUser();

            // When
            UserResponse response = accountController.me();

            // Then
            assertNotNull(response);
            assertEquals(user.getId(), response.getId());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(user.getTitle(), response.getTitle());
            assertEquals(user.getName(), response.getName());
            assertEquals(user.getLastName(), response.getLastName());
            assertEquals(user.getRoles().stream().
                    map(role -> role.getName().toString()).toList(), response.getRoles());
        }
    }

    @Nested
    @DisplayName("Test class for update profile scenarios")
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UpdateProfileTest {
        @Test
        void givenUpdateProfileRequest_whenUpdateProfile_thenAssertBody() {
            // Given
            UpdateProfileRequest request = Factory.createUpdateProfileRequest();
            request.setEmail("valid.email@example.com");

            User user = Factory.createUser();

            doReturn(user).when(userService).getUser();
            when(userService.updateProfile(user.getId(), request)).thenReturn(user);

            // When
            UserResponse response = accountController.profile(request);

            // Then
            assertNotNull(response);
            assertEquals(user.getId(), response.getId());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(user.getTitle(), response.getTitle());
            assertEquals(user.getName(), response.getName());
            assertEquals(user.getLastName(), response.getLastName());
            assertEquals(user.getRoles().stream()
                    .map(role -> role.getName().toString())
                    .toList(), response.getRoles());
        }
    }

    @Nested
    @DisplayName("Test class for update password scenarios")
    class UpdatePasswordTest {
        @Test
        void givenUpdatePasswordRequest_whenUpdatePassword_thenAssertBody() {
            // Given
            UpdatePasswordRequest request = Factory.createUpdatePasswordRequest();
            User user = Factory.createUser();
            request.setOldPassword(user.getPassword());
            request.setPassword("newPass");
            request.setPasswordConfirm("newPass");
            doReturn(user).when(userService).getUser();
            doReturn("Password updated successfully.").when(messageSourceService).get("your_password_updated");
            doNothing().when(userService).updatePassword(user.getId(), request);

            // When
            SuccessResponse response = accountController.password(request);

            // Then
            assertNotNull(response);
            assertEquals("Password updated successfully.", response.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for resend gsm activation scenarios")
    class ResendGsmActivationTest {
        @Test
        void givenValidUser_whenResendGsmActivation_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            doReturn(user).when(userService).getUser();
            doReturn("GSM activation code sent successfully.").when(messageSourceService).get("gsm_activation_code_sent");
            doNothing().when(userService).resendGsmActivationById(user.getId());

            // When
            SuccessResponse response = accountController.resendGsmActivation();

            // Then
            assertNotNull(response);
            assertEquals("GSM activation code sent successfully.", response.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for activate gsm scenarios")
    class ActivateGsmTest {
        @Test
        void givenActivateGsmRequest_whenActivateGsm_thenAssertBody() {
            // Given
            ActivateGsmRequest request = Factory.createActivateGsmRequest();
            User user = Factory.createUser();
            request.setToken(new RandomStringGenerator(GSM_ACTIVATION_TOKEN_LENGTH, true).next());
            doReturn(user).when(userService).getUser();
            doReturn("Your GSM number activated successfully!").when(messageSourceService).get("your_gsm_activated");
            doNothing().when(userService).activateGsm(user.getId(), request.getToken());

            // When
            SuccessResponse response = accountController.activateGsm(request);

            // Then
            assertNotNull(response);
            assertEquals("Your GSM number activated successfully!", response.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for Update avatar")
    class UpdateAvatarTest {
        @Test
        void givenAvatar_whenUpdateAvatar_thenThrowBadRequestException() {
            // Given
            MockMultipartFile invalidAvatar = new MockMultipartFile("avatar", "avatar.txt",
                    "text/plain", new byte[]{1, 2, 3});

            doReturn(Factory.createUser()).when(userService).getUser();

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> accountController.updateAvatar(invalidAvatar));
            assertEquals("Invalid image format. Allowed formats: PNG, JPEG.", exception.getMessage());
        }

        @Test
        void givenAvatar_whenUpdateAvatar_thenAssertBody() {
        }
    }

//    @Nested
//    @DisplayName("Test class for update avatar scenarios")
//    class UpdateAvatarTest {
//        @Test
//        void givenUpdateAvatarRequest_whenUpdateAvatar_thenAssertBody() {
//            // Given
//            UpdateAvatarRequest request = Factory.createUpdateAvatarRequest();
//            User user = Factory.createUser();
//            doReturn(user).when(userService).getUser();
//            when(userService.updateAvatar(user.getId(), request)).thenReturn(user);
//
//            // When
//            AvatarResponse response = accountController.avatar(request);
//
//            // Then
//            assertNotNull(response);
//        }
//    }

//    @Nested
//    @DisplayName("Test class for delete avatar scenarios")
//    class DeleteAvatarTest {
//        @Test
//        void givenUserId_whenDeleteAvatar_thenAssertBody() {
//            // Given
//            User user = Factory.createUser();
//            doReturn(user).when(userService).getUser();
//            doNothing().when(userService).deleteAvatar(user.getId());
//            // When
//            ResponseEntity<Void> response = accountController.avatar();
//            // Then
//            Assert.notNull(response);
//            Assertions.assertNull(response.getBody());
//            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        }
//    }
}

