package com.cosmosboard.fmh.controller.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cosmosboard.fmh.dto.request.user.CreateUserRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateAvatarRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateUserRequest;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.user.AvatarResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.dto.response.user.UsersPaginationResponse;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.UserCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.UserService;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Admin - UserController")
class UserControllerTest {
    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    @Mock
    MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("Test class for user list scenarios")
    public class ListTest {
        @Test
        void givenRolesAndGendersAndBirthDateStartAndBirthDateEndAndCreatedAtStartAndCreatedAtEndAndIsAvatarAndIsEmailActivatedAndIsBLockedAndQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenBadRequestExceptionThrown() {
            // Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("TEST Invalid Sort Column");

            // When
            Executable response = () -> userController.list(null, null, null, null,
                    null, null, null, null, null, "InvalidSortBy",
                    null);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST Invalid Sort Column", exception.getMessage());
        }

        @Test
        void givenRolesAndGendersAndBirthDateStartAndBirthDateEndAndCreatedAtStartAndCreatedAtEndAndIsAvatarAndIsEmailActivatedAndIsBLockedAndQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Page<User> page = new PageImpl<>(List.of(user));
            doReturn(page).when(userService).findAll(any(UserCriteria.class),
                    any(PaginationCriteria.class));
            /*
             * NOT: doReturn yerine bu şekilde kullanmak daha doğru.
             * when(userService.all(Mockito.any(UserCriteria.class),
             * Mockito.any(PaginationCriteria.class)))
             * .thenReturn(page);
             */
            // When
            UsersPaginationResponse response = userController.list(null, null,
                    null, null, null, null, null,
                    null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(1, response.getPage());
            assertEquals(1, response.getPages());
            assertEquals(1, response.getTotal());
            assertEquals(1, response.getItems().size());

            UserResponse userResponse = response.getItems().get(0);
            assertEquals(user.getId(), userResponse.getId());
            assertEquals(user.getEmail(), userResponse.getEmail());
            assertEquals(user.getTitle(), userResponse.getTitle());
            assertEquals(user.getName(), userResponse.getName());
            assertEquals(user.getLastName(), userResponse.getLastName());
            assertNotNull(user.getRoles());
            assertEquals(user.getRoles().stream().map(role -> role.getName().toString()).toList(),
                    userResponse.getRoles());
        }
    }

    @Nested
    @DisplayName("Test class for user create scenarios")
    public class CreateTest {
        @Test
        void givenCreateUserRequest_whenCreate_thenBadRequestException() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            when(userService.existsByEmail(request.getEmail())).thenReturn(true);

            // When and Then
            assertThrows(BadRequestException.class, () -> userController.create(request));
        }

        @Test
        void givenCreateUserRequest_whenCreate_thenAssertBody() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            User user = Factory.createUser();
            when(userService.create(request)).thenReturn(user);
            // When
            ResponseEntity<UserResponse> response = userController.create(request);
            // Then
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getHeaders());
            assertNull(response.getHeaders().getLocation());
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Test class for user show scenarios")
    public class ShowTest {
        @Test
        void givenStringId_whenShow_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            when(userService.findOneById(user.getId())).thenReturn(user);

            // When
            UserResponse response = userController.show(user.getId());

            // Then
            assertNotNull(response);
            assertEquals(user.getId(), response.getId());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(user.getTitle(), response.getTitle());
            assertEquals(user.getName(), response.getName());
            assertEquals(user.getLastName(), response.getLastName());
            assertEquals(user.getRoles().stream().map(role -> role.getName().toString()).toList(), response.getRoles());
        }
    }

    @Nested
    @DisplayName("Test class for user create scenarios")
    public class UpdateTest {
        @Test
        void givenExistByEmail_whenUpdate_thenBadRequestException() {
            // Given
            User existingUser = Factory.createUser();
            when(userService.existsByEmail(existingUser.getEmail())).thenReturn(true);
            when(messageSourceService.get("email_already_using")).thenReturn("TEST Email Already Using");
            UpdateUserRequest request = Factory.createUpdateUserRequest();
            request.setEmail(existingUser.getEmail());

            // When
            Executable response = () -> userController.update(existingUser.getId(), request);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST Email Already Using", exception.getMessage());
        }

        @Test
        void givenUpdateUserRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateUserRequest request = Factory.createUpdateUserRequest();
            User user = Factory.createUser();
            when(userService.update(user.getId(), request)).thenReturn(user);

            // When
            UserResponse response = userController.update(user.getId(), request);

            // Then
            assertNotNull(response);
            assertEquals(response.getId(), user.getId());
            assertEquals(response.getEmail(), user.getEmail());
            assertEquals(response.getTitle(), user.getTitle());
            assertEquals(response.getName(), user.getName());
            assertEquals(response.getLastName(), user.getLastName());
            assertEquals(response.getRoles(), user.getRoles().stream().map(role -> role.getName().toString()).toList());
        }
    }

    @Nested
    @DisplayName("Test class for user resend activation scenarios")
    public class ResendActivationTest {
        @Test
        void givenStringId_whenResendActivation_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            String message = "Activation e-mail sent!";
            when(messageSourceService.get("activation_email_sent")).thenReturn(message);

            // When
            SuccessResponse response = userController.resendActivation(user.getId());

            // Then
            assertNotNull(response);
            assertEquals(message, response.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for user avatar scenarios")
    public class AvatarTest {
        @Test
        void givenStringIdAndUpdateAvatarRequest_whenAvatar_thenAssertBody() {
            // Given
            UpdateAvatarRequest request = Factory.createUpdateAvatarRequest();
            User user = Factory.createUser();
            when(userService.updateAvatar(user.getId(), request.getFile())).thenReturn(user);

            // When
            AvatarResponse response = userController.avatar(user.getId(), request);

            // Then
            assertNotNull(response);
            assertEquals(user.getAvatar(), response.getAvatar());
        }
    }

    @Nested
    @DisplayName("Test class for user delete avatar scenarios")
    public class DeleteAvatarTest {
        @Test
        void givenStringId_whenDeleteAvatar_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            // When
            ResponseEntity<Void> response = userController.avatar(user.getId());
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Test class for user delete scenarios")
    public class DeleteTest {
        @Test
        void givenStringId_whenDelete_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            // When
            ResponseEntity<Void> response = userController.delete(user.getId());
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }
}
