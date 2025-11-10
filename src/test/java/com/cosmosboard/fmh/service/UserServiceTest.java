package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.auth.RegisterRequest;
import com.cosmosboard.fmh.dto.request.user.ChangePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.CreateUserRequest;
import com.cosmosboard.fmh.dto.request.user.UpdatePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateProfileRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateUserRequest;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.EmailActivationToken;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.GsmActivationToken;
import com.cosmosboard.fmh.entity.PasswordResetToken;
import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.UserCriteria;
import com.cosmosboard.fmh.event.UserGsmActivationSendEvent;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.UserRepository;
import com.cosmosboard.fmh.repository.redis.PasswordResetTokenRepository;
import com.cosmosboard.fmh.security.JwtUserDetails;
import com.cosmosboard.fmh.service.storage.StorageService;
import com.cosmosboard.fmh.service.storage.StorageThumbnailService;
import com.cosmosboard.fmh.util.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static com.cosmosboard.fmh.util.AppConstants.AVATAR_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for UserService")
public class UserServiceTest {
    @InjectMocks private UserService userService;

    @Mock private UserRepository userRepository;

    @Mock private MessageSourceService messageSourceService;

    @Mock private Authentication authentication;

    @Mock private SecurityContext securityContext;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private RoleService roleService;

    @Mock private CompanyService companyService;

    @Mock private EmailActivationTokenService emailActivationTokenService;

    @Mock private ApplicationEventPublisher eventPublisher;

    @Mock private GsmActivationTokenService gsmActivationTokenService;

    @Mock private StorageService storageService;

    @Mock private EmailService emailService;

    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock private StorageThumbnailService storageThumbnailService;

    public static final String PICTURES_PATH = "pictures";

    public static final String AVATARS_PATH = String.format("%s/avatars", PICTURES_PATH);

    @Nested
    @DisplayName("Test class for loadUserByUsername scenarios")
    class LoadUserByUsernameTest {
        @Test
        void givenString_whenLoadUserByUsername_thenAssertBody() {
            // Given
            String validUsername = "test";
            User mockUser = Factory.createUser();

            when(userRepository.findByEmail(validUsername)).thenReturn(Optional.of(mockUser));

            // When
            UserDetails userDetails = userService.loadUserByUsername(validUsername);

            // Then
            assertEquals(mockUser.getEmail(), userDetails.getUsername());
        }
    }

    @Nested
    @DisplayName("Test class for loadUserById scenarios")
    class LoadUserByIdTest {
        @Test
        void givenString_whenLoadUserById_thenAssertBody() {
            // Given
            String validUserId = "validUserId";
            User mockUser = Factory.createUser();
            mockUser.setId(validUserId);

            when(userRepository.findById(validUserId)).thenReturn(Optional.of(mockUser));

            // When
            UserDetails userDetails = userService.loadUserById(validUserId);

            // Then
            assertNotNull(userDetails);
            assertEquals(mockUser.getId(), ((JwtUserDetails) userDetails).getId());
        }
    }

    @Nested
    @DisplayName("Test class for getAuthentication scenarios")
    class GetAuthenticationTest {
        @Test
        void given_whenGetAuthentication_thenAssertBody() {
            // Given
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            // When
            Authentication authentication = userService.getAuthentication();

            // Then
            assertNotNull(authentication);
        }
    }

    @Nested
    @DisplayName("Test class for getUser scenarios")
            //TODO:jwtUserDetails mocklama sorunu burda ve bir kaç testde alıyorum
            //GetUserTest ve GetPrincipalTest yazılacak
    class GetUserTest {
        @Test
        void given_whenGetUser_thenAssertBody() {
        }
    }

    @Nested
    @DisplayName("Test class for getPrincipal scenarios")
    class GetPrincipalTest {
        @Test
        void givenAuthentication_whenGetPrincipal_thenAssertBody() {

        }
    }

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            long expectedUserCount = 5;
            when(userRepository.count()).thenReturn(expectedUserCount);

            // When
            long actualUserCount = userService.count();

            // Then
            assertEquals(expectedUserCount, actualUserCount);
        }
    }

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void givenUserCriteriaAndPaginationCriteria_whenFindAll_thenAssertBody() {
            // Given
            UserCriteria userCriteria = Factory.createUserCriteria();
            PaginationCriteria paginationCriteria = Factory.createPaginationCriteria();
            User user = Factory.createUser();
            Page<User> mockPage = new PageImpl<>(Collections.singletonList(user));
            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(mockPage);

            // When
            Page<User> resultPage = userService.findAll(userCriteria, paginationCriteria);

            // Then
            assertNotNull(resultPage);
            assertEquals(1, resultPage.getTotalElements());
            assertEquals(user, resultPage.getContent().get(0));
            verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // When
            User result = userService.findOneById(user.getId());

            // Then
            assertNotNull(result);
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getName(), result.getName());
            verify(userRepository, times(1)).findById(user.getId());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // Given
            String userId = "nonexistentUser";
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            when(messageSourceService.get("user_not_found")).thenReturn("Test");

            // When, Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.findOneById(userId);
            });

            assertEquals("Test", exception.getMessage());
            verify(userRepository, times(1)).findById(userId);
        }

    }

    @Nested
    @DisplayName("Test class for findOneByEmail scenarios")
    class FindOneByEmailTest {
        @Test
        void givenEmail_whenFindOneByEmail_thenAssertBody() {
            // Given
            User mockUser = Factory.createUser();
            when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

            // When
            User resultUser = userService.findOneByEmail(mockUser.getEmail());

            // Then
            assertEquals(mockUser, resultUser);
        }

        @Test
        void givenEmail_whenFindOneByEmail_thenThrowNotFoundException() {
            // Given
            String nonexistentEmail = "nonexistent@example.com";

            when(userRepository.findByEmail(nonexistentEmail)).thenReturn(Optional.empty());
            when(messageSourceService.get("user_not_found")).thenReturn("Test");

            // When, Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.findOneByEmail(nonexistentEmail);
            });

            assertEquals("Test", exception.getMessage());
        }

    }

    @Nested
    @DisplayName("Test class for findOneByGsm scenarios")
    class FindOneByGsmTest {

        @Test
        void givenGsm_whenFindOneByGsm_thenAssertBody() {
            // Given
            String gsm = "5551234567";
            User user = Factory.createUser();  // Gerekirse burada gsm numarasını kullanıcıya set edebilirsiniz
            when(userRepository.findByGsm(gsm)).thenReturn(Optional.of(user));

            // When
            User result = userService.findOneByGsm(gsm);

            // Then
            assertNotNull(result);
            assertEquals(user.getGsm(), result.getGsm());
            assertEquals(user.getId(), result.getId());
            assertEquals(user.getName(), result.getName());
            verify(userRepository, times(1)).findByGsm(gsm);
        }

        @Test
        void givenGsm_whenFindOneByGsm_thenThrowNotFoundException() {
            // Given
            String gsm = "5551234567";
            when(userRepository.findByGsm(gsm)).thenReturn(Optional.empty());
            when(messageSourceService.get("user_not_found")).thenReturn("Test");

            // When, Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.findOneByGsm(gsm);
            });

            assertEquals("Test", exception.getMessage());
            verify(userRepository, times(1)).findByGsm(gsm);
        }
    }

    @Nested
    @DisplayName("Test class for existsByEmail scenarios")
    class ExistsByEmailTest {
        @Test
        void givenEmail_whenExistsByEmail_thenAssertTrue() {
            // Given
            String email = "test@example.com";
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // When
            boolean exists = userService.existsByEmail(email);

            // Then
            assertTrue(exists);
        }

        @Test
        void givenEmail_whenExistsByEmail_thenAssertFalse() {
            // Given
            String email = "nonexistent@example.com";
            when(userRepository.existsByEmail(email)).thenReturn(false);

            // When
            boolean exists = userService.existsByEmail(email);

            // Then
            assertFalse(exists);
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenCreateUserRequest_whenCreate_thenAssertBody() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            request.setRoles(Arrays.asList("USER", "ADMIN"));
            request.setIsEmailActivated(true);
            request.setIsGsmActivated(true);
            request.setIsBlocked(false);

            User user = Factory.createUser();
            user.setRoles(Arrays.asList(new Role(AppConstants.RoleEnum.USER), new Role(AppConstants.RoleEnum.ADMIN)));
            user.setEmailActivatedAt(LocalDateTime.now());
            user.setGsmActivatedAt(LocalDateTime.now());
            user.setBlockedAt(null);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.create(request);

            // Then
            assertNotNull(result);
            assertEquals(request.getRoles().size(), result.getRoles().size());
            assertEquals(request.getIsEmailActivated(), result.getEmailActivatedAt() != null);
            assertEquals(request.getIsGsmActivated(), result.getGsmActivatedAt() != null);
            assertNull(result.getBlockedAt());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void givenCreateUserRequest_whenCreateWithNullActivationFlags_thenReturnCreatedUserWithNullDates() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            request.setRoles(List.of("USER"));
            request.setIsEmailActivated(null);
            request.setIsGsmActivated(null);
            request.setIsBlocked(null);

            User user = Factory.createUser();
            user.setRoles(List.of(new Role(AppConstants.RoleEnum.USER)));
            user.setEmailActivatedAt(null);
            user.setGsmActivatedAt(null);
            user.setBlockedAt(null);

            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.create(request);

            // Then
            assertNotNull(result);
            assertNull(result.getEmailActivatedAt());
            assertNull(result.getGsmActivatedAt());
            assertNull(result.getBlockedAt());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void givenCreateUserRequest_whenCreateWithEmailActivatedFalse_thenReturnCreatedUserWithNullEmailDate() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            request.setRoles(List.of("USER"));
            request.setIsEmailActivated(false);
            request.setIsGsmActivated(true);
            request.setIsBlocked(false);

            User user = Factory.createUser();
            user.setRoles(List.of(new Role(AppConstants.RoleEnum.USER)));
            user.setEmailActivatedAt(null);
            user.setGsmActivatedAt(LocalDateTime.now());
            user.setBlockedAt(null);

            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.create(request);

            // Then
            assertNotNull(result);
            assertNull(result.getEmailActivatedAt());
            assertNotNull(result.getGsmActivatedAt());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void givenCreateUserRequest_whenCreateWithGsmActivatedFalse_thenReturnCreatedUserWithNullGsmDate() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            request.setRoles(List.of("USER"));
            request.setIsEmailActivated(true);
            request.setIsGsmActivated(false);
            request.setIsBlocked(false);

            User user = Factory.createUser();
            user.setRoles(List.of(new Role(AppConstants.RoleEnum.USER)));
            user.setEmailActivatedAt(LocalDateTime.now());
            user.setGsmActivatedAt(null);
            user.setBlockedAt(null);

            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.create(request);

            // Then
            assertNotNull(result);
            assertNotNull(result.getEmailActivatedAt());
            assertNull(result.getGsmActivatedAt());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void givenCreateUserRequest_whenCreateWithBlockedFalse_thenReturnCreatedUserWithNullBlockedDate() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            request.setRoles(List.of("USER"));
            request.setIsEmailActivated(true);
            request.setIsGsmActivated(true);
            request.setIsBlocked(false);

            User user = Factory.createUser();
            user.setRoles(List.of(new Role(AppConstants.RoleEnum.USER)));
            user.setEmailActivatedAt(LocalDateTime.now());
            user.setGsmActivatedAt(LocalDateTime.now());
            user.setBlockedAt(null);

            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.create(request);

            // Then
            assertNotNull(result);
            assertNull(result.getBlockedAt());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void givenCreateUserRequest_whenCreateWithBlockedTrue_thenSetBlockedAt() {
            // Given
            CreateUserRequest request = Factory.createCreateUserRequest();
            request.setRoles(List.of("USER"));
            request.setIsEmailActivated(true);
            request.setIsGsmActivated(true);
            request.setIsBlocked(true);

            User user = Factory.createUser();
            user.setRoles(List.of(new Role(AppConstants.RoleEnum.USER)));
            user.setEmailActivatedAt(LocalDateTime.now());
            user.setGsmActivatedAt(LocalDateTime.now());
            user.setBlockedAt(LocalDateTime.now());

            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.create(request);

            // Then
            assertNotNull(result);
            assertNotNull(result.getBlockedAt());
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test class for createEmployee scenarios")
    class CreateEmployeeTest {
        @Test
        void givenRegisterRequestWithAvatar_whenCreateEmployee_thenReturnUserWithAvatarAndTokens()  {
            RegisterRequest request = Factory.createRegisterRequest();
            MultipartFile avatar = mock(MultipartFile.class);

            when(roleService.findOneByName(AppConstants.RoleEnum.USER)).thenReturn(new Role(AppConstants.RoleEnum.USER));
            when(storageService.store(any(MultipartFile.class), eq(AVATARS_PATH), eq(true))).thenReturn("avatar.jpg");
            when(storageThumbnailService.make(anyString())).thenReturn(storageThumbnailService);
            when(storageThumbnailService.resize(eq(AVATAR_SIZE), eq(AVATAR_SIZE))).thenReturn(storageThumbnailService);
            doNothing().when(storageThumbnailService).save(anyString());

            EmailActivationToken emailActivationToken = Factory.createEmailActivationToken();
            GsmActivationToken gsmActivationToken = Factory.createGsmActivationToken();
            when(emailActivationTokenService.create(any(User.class))).thenReturn(emailActivationToken);
            when(gsmActivationTokenService.create(any(User.class))).thenReturn(gsmActivationToken);

            User user = Factory.createUser();
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = userService.createEmployee(request);

            assertNotNull(result);
//            assertEquals(emailActivationToken.getToken(), result.getEmailActivationToken());
            assertEquals(gsmActivationToken.getToken(), result.getGsmActivationToken());
            verify(userRepository, times(1)).save(any(User.class));
            verify(storageService, times(1)).store(any(MultipartFile.class), eq(AVATARS_PATH), eq(true));
            verify(storageThumbnailService, times(1)).save(anyString());
        }
    }

    @Nested
    @DisplayName("Test class for resendEmailActivationByEmail scenarios")
    class ResendEmailActivationByEmailTest {
        @Test
        void  givenEmail_whenResendEmailActivationByEmail_thenNotFoundException() {
            // Given
            String invalidEmail = "invalid@example.com";

            when(userRepository.findByEmail(invalidEmail)).thenReturn(java.util.Optional.empty());

            // When, Then
            assertThrows(NotFoundException.class, () -> userService.resendEmailActivationByEmail(invalidEmail));
        }

        @Test
        void  givenEmail_whenResendEmailActivationByEmail_thenBadRequestException() {
            // Given
            String activatedEmail = "activated@example.com";
            User user = Factory.createUser();
            user.setEmail(activatedEmail);
            user.setEmailActivatedAt(LocalDateTime.now()); // Simulate an already activated email

            when(userRepository.findByEmail(activatedEmail)).thenReturn(java.util.Optional.of(user));

            // When, Then
            assertThrows(BadRequestException.class, () -> userService.resendEmailActivationByEmail(activatedEmail));
        }

        @Test
        void  givenEmail_whenResendEmailActivationByEmail_thenAssertBody() {
            // Given
            String validEmail = "test@example.com";
            User user = new User();
            user.setEmail(validEmail);

            when(userRepository.findByEmail(validEmail)).thenReturn(java.util.Optional.of(user));

            // When
            assertDoesNotThrow(() -> userService.resendEmailActivationByEmail(validEmail));

        }
    }

    @Nested
    @DisplayName("Test class for resendEmailActivationById scenarios")
    class ResendEmailActivationByIdTest {
        @Test
        void  givenId_whenResendEmailActivationById_thenNotFoundException() {
            // Given
            String invalidId = "TestId";

            when(userRepository.findById(invalidId)).thenReturn(java.util.Optional.empty());

            // When, Then
            assertThrows(NotFoundException.class, () -> userService.resendEmailActivationById(invalidId));
        }

        @Test
        void  givenId_whenResendEmailActivationById_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            user.setEmailActivatedAt(LocalDateTime.now());

            when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

            // When, Then
            assertThrows(BadRequestException.class, () -> userService.resendEmailActivationById(user.getId()));
        }

        @Test
        void  givenId_whenResendEmailActivationById_thenAssertBody() {
            // Given
            String validUserId = "12345";
            User user = new User();
            user.setId(validUserId);

            when(userRepository.findById(validUserId)).thenReturn(java.util.Optional.of(user));

            // When
            assertDoesNotThrow(() -> userService.resendEmailActivationById(validUserId));


        }
    }

    @Nested
    @DisplayName("Test class for resendGsmActivationByGsm scenarios")
    class ResendGsmActivationByGsmTest {
        @Test
        void givenEmailAndGsm_whenResendGsmActivationByGsm_thenReturnUser() {
            // Given
            String email = "test@example.com";
            String gsm = "5551234567";
            User user = Factory.createUser();
            user.setEmail(email);
            user.setGsm(gsm);
            user.setGsmActivatedAt(null);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            doNothing().when(eventPublisher).publishEvent(any(UserGsmActivationSendEvent.class));
            when(gsmActivationTokenService.create(user)).thenReturn(Factory.createGsmActivationToken());

            // When
            User result = userService.resendGsmActivationByGsm(email, gsm);

            // Then
            assertNotNull(result);
            assertEquals(user, result);
            verify(userRepository, times(1)).findByEmail(email);
            verify(gsmActivationTokenService, times(1)).create(user);
            verify(eventPublisher, times(1)).publishEvent(any(UserGsmActivationSendEvent.class));
        }

        @Test
        void givenEmailAndGsm_whenResendGsmActivationByGsm_thenThrowNotFoundException() {
            // Given
            String email = "test@example.com";
            String gsm = "5551234567";
            User user = Factory.createUser();
            user.setEmail(email);
            user.setGsm("5559876543");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(messageSourceService.get("user_not_found")).thenReturn("user_not_found");

            // When & Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.resendGsmActivationByGsm(email, gsm);
            });

            assertEquals("user_not_found", exception.getMessage());
            verify(userRepository, times(1)).findByEmail(email);
            verify(gsmActivationTokenService, never()).create(any(User.class));
        }

        @Test
        void givenAlreadyActivatedGsm_whenResendGsmActivationByGsm_thenThrowBadRequestException() {
            // Given
            String email = "test@example.com";
            String gsm = "5551234567";
            User user = Factory.createUser();
            user.setEmail(email);
            user.setGsm(gsm);
            user.setGsmActivatedAt(LocalDateTime.now());

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(messageSourceService.get("this_gsm_already_activated")).thenReturn("this_gsm_already_activated");

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                userService.resendGsmActivationByGsm(email, gsm);
            });

            assertEquals("this_gsm_already_activated", exception.getMessage());
            verify(userRepository, times(1)).findByEmail(email);
            verify(gsmActivationTokenService, never()).create(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test class for resendGsmActivationById scenarios")
    class ResendGsmActivationByIdTest {

        @Test
        void givenId_whenResendGsmActivationById_thenReturnUser() {
            // Given
            String id = "test@example.com";
            User user = Factory.createUser();
            user.setEmail(id);
            user.setGsm("5551234567");
            user.setGsmActivatedAt(null);

            when(userRepository.findByEmail(id)).thenReturn(Optional.of(user));
            doNothing().when(eventPublisher).publishEvent(any(UserGsmActivationSendEvent.class));
            when(gsmActivationTokenService.create(user)).thenReturn(Factory.createGsmActivationToken());

            // When
            userService.resendGsmActivationById(id);

            // Then
            verify(userRepository, times(1)).findByEmail(id);
            verify(gsmActivationTokenService, times(1)).create(user);
            verify(eventPublisher, times(1)).publishEvent(any(UserGsmActivationSendEvent.class));
        }

        @Test
        void givenId_whenResendGsmActivationById_thenThrowNotFoundException() {
            // Given
            String id = "test@example.com";
            when(userRepository.findByEmail(id)).thenReturn(Optional.empty());
            when(messageSourceService.get("user_not_found")).thenReturn("user_not_found");

            // When & Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.resendGsmActivationById(id);
            });

            assertEquals("user_not_found", exception.getMessage());
            verify(userRepository, times(1)).findByEmail(id);
            verify(gsmActivationTokenService, never()).create(any(User.class));
        }

        @Test
        void givenId_whenGsmAlreadyActivated_thenThrowBadRequestException() {
            // Given
            String id = "test@example.com";
            User user = Factory.createUser();
            user.setEmail(id);
            user.setGsm("5551234567");
            user.setGsmActivatedAt(LocalDateTime.now());

            when(userRepository.findByEmail(id)).thenReturn(Optional.of(user));
            when(messageSourceService.get("this_gsm_already_activated")).thenReturn("this_gsm_already_activated");

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                userService.resendGsmActivationById(id);
            });

            assertEquals("this_gsm_already_activated", exception.getMessage());
            verify(userRepository, times(1)).findByEmail(id);
            verify(gsmActivationTokenService, never()).create(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test class for activateEmail scenarios")
    class ActivateEmailTest {
        @Test
        void givenToken_whenActivateEmail_thenAssertBody() {
            // Given
            String token = "valid-token";
            EmailActivationToken emailActivationToken = Factory.createEmailActivationToken();
            emailActivationToken.setToken(token);
            emailActivationToken.setUserId("test@example.com");

            User user = Factory.createUser();
            user.setId("test@example.com");
            user.setEmailActivatedAt(null);

            when(emailActivationTokenService.getUserByToken(token)).thenReturn(emailActivationToken);
            when(userRepository.findById(emailActivationToken.getUserId())).thenReturn(Optional.of(user));
            doNothing().when(emailActivationTokenService).delete(emailActivationToken);
            when(userRepository.save(user)).thenReturn(user);

            // When
            userService.activateEmail(token);

            // Then
            assertNotNull(user.getEmailActivatedAt());
            verify(emailActivationTokenService, times(1)).getUserByToken(token);
            verify(emailActivationTokenService, times(1)).delete(emailActivationToken);
            verify(userRepository, times(1)).findById(emailActivationToken.getUserId());
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    @DisplayName("Test class for activateGsm scenarios")
    class ActivateGsmTest {
        @Test
        void givenUserIdAndToken_whenActivateGsm_thenAssertBody() {
            // Given
            String userId = "test@example.com";
            String token = "valid-token";
            GsmActivationToken gsmActivationToken = Factory.createGsmActivationToken();
            gsmActivationToken.setUserId(userId);
            gsmActivationToken.setToken(token);

            User user = Factory.createUser();
            user.setId(userId);
            user.setGsmActivatedAt(null);

            when(gsmActivationTokenService.getUserByUserIdAndToken(userId, token)).thenReturn(gsmActivationToken);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            doNothing().when(gsmActivationTokenService).delete(gsmActivationToken);
            when(userRepository.save(user)).thenReturn(user);

            // When
            userService.activateGsm(userId, token);

            // Then
            assertNotNull(user.getGsmActivatedAt());
            verify(gsmActivationTokenService, times(1)).getUserByUserIdAndToken(userId, token);
            verify(gsmActivationTokenService, times(1)).delete(gsmActivationToken);
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).save(user);
        }


        @Test
        void givenMismatchedUserIdAndToken_whenActivateGsm_thenThrowNotFoundException() {
            // Given
            String userId = "test@example.com";
            String token = "valid-token";
            GsmActivationToken gsmActivationToken = new GsmActivationToken();
            gsmActivationToken.setUserId("another@example.com");
            gsmActivationToken.setToken(token);

            User user = Factory.createUser();
            user.setId(userId);
            user.setGsmActivatedAt(null);

            when(gsmActivationTokenService.getUserByUserIdAndToken(userId, token)).thenReturn(gsmActivationToken);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // When & Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.activateGsm(userId, token);
            });

            assertEquals("User not found", exception.getMessage());
            verify(gsmActivationTokenService, times(1)).getUserByUserIdAndToken(userId, token);
            verify(gsmActivationTokenService, never()).delete(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Test class for updateProfile scenarios")
    class UpdateProfileTest {
        @Test
        void givenIdAndUpdateProfileRequest_whenUpdateProfile_thenAssertBody() {
            // Given
            String userId = "test@example.com";
            UpdateProfileRequest request = Factory.createUpdateProfileRequest();
            request.setEmail("newemail@example.com");
            request.setGsm("5551234567");
            request.setName("John");
            request.setLastName("Doe");
            request.setTitle("Mr");

            User user = Factory.createUser();
            user.setId(userId);
            user.setEmail("test@example.com");
            user.setGsm("5559876543");
            user.setName("Jane");
            user.setLastName("Doe");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(request.getEmail(), userId)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User updatedUser = userService.updateProfile(userId, request);

            // Then
            assertNotNull(updatedUser);
            assertEquals("newemail@example.com", updatedUser.getEmail());
            assertEquals("5551234567", updatedUser.getGsm());
            assertEquals("John", updatedUser.getName());
            assertEquals("Doe", updatedUser.getLastName());
            assertEquals("Mr", updatedUser.getTitle());
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void givenIdAndUpdateProfileRequest_whenUpdateProfile_thenBadRequestException() {
            // Given
            String userId = "test@example.com";
            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setEmail("existingemail@example.com");

            User user = Factory.createUser();
            user.setId(userId);
            user.setEmail("test@example.com");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(request.getEmail(), userId)).thenReturn(true);
            when(messageSourceService.get("unique_email")).thenReturn("unique_email");

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                userService.updateProfile(userId, request);
            });

            assertEquals("unique_email", exception.getMessage());
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, never()).save(any());
        }

        @Test
        void givenGsmIsEmpty_whenUpdateProfile_thenSetGsmToNull() {
            // Given
            String userId = "test@example.com";
            UpdateProfileRequest request = Factory.createUpdateProfileRequest();
            request.setGsm("");

            User user = Factory.createUser();
            user.setId(userId);
            user.setGsm("5559876543");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User updatedUser = userService.updateProfile(userId, request);

            // Then
            assertNull(updatedUser.getGsm());
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    @DisplayName("Test class for updateAvatar scenarios")
    class UpdateAvatarTest {
        @Test
        void givenIdAndUpdateAvatarRequest_whenUpdateAvatar_thenAssertBody() {
            //TODO: yazılacak
        }
    }

    @Nested
    @DisplayName("Test class for deleteAvatar scenarios")
    class DeleteAvatarTest {
        @Test
        void givenId_whenDeleteAvatar_thenAssertBody() {
            // Given
            String userId = "validUserId";
            User existingUser = Factory.createUser();
            existingUser.setId(userId);
            existingUser.setAvatar("oldAvatar.jpg");

            // When
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            userService.deleteAvatar(userId);

            // Then
            assertNull(existingUser.getAvatar());
            verify(storageService, times(1)).delete(Paths.get(AVATARS_PATH, "oldAvatar.jpg").toString());
        }

        @Test
        void givenId_whenDeleteAvatar_thenNotFoundException() {
            // Given
            String userId = "validUserId";
            User existingUser = Factory.createUser();
            existingUser.setId(userId);
            existingUser.setAvatar(null);

            // When
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(messageSourceService.get("avatar_not_found")).thenReturn("Avatar not found");

            // Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                userService.deleteAvatar(userId);
            });

            assertEquals("Avatar not found", exception.getMessage());
            verify(storageService, never()).delete(anyString());
            verify(userRepository, never()).save(existingUser);
        }

        @Test
        void givenId_whenDeleteAvatar_thenAvatarIsDeleted() {
            // Given
            String userId = "validUserId";
            User existingUser = Factory.createUser();
            existingUser.setId(userId);
            existingUser.setAvatar("oldAvatar.jpg");

            // When
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            userService.deleteAvatar(userId);

            // Then
            assertNull(existingUser.getAvatar());
            verify(storageService, times(1)).delete(Paths.get(AVATARS_PATH, "oldAvatar.jpg").toString());  // Avatar file is deleted
            verify(userRepository, times(1)).save(existingUser);
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenIdAndUpdateUserRequest_whenUpdate_thenAssertBody() {
            // Given
            String userId = "validUserId";
            UpdateUserRequest request = Factory.createUpdateUserRequest();
            request.setEmail("newemail@example.com");
            request.setGsm("");
            request.setTitle("");
            request.setName("John");
            request.setLastName("Doe");
            request.setIsEmailActivated(true);
            request.setIsGsmActivated(true);
            request.setIsBlocked(true);
            List<String> roles = List.of("ADMIN", "USER");
            request.setRoles(roles);

            User existingUser = Factory.createUser();
            existingUser.setId(userId);
            existingUser.setEmail("oldemail@example.com");
            existingUser.setGsm("9876543210");
            existingUser.setTitle("Ms.");
            existingUser.setName("Alice");
            existingUser.setLastName("Smith");
            existingUser.setEmailActivatedAt(LocalDateTime.of(2022, 1, 1, 0, 0));
            existingUser.setGsmActivatedAt(LocalDateTime.of(2022, 2, 1, 0, 0));
            existingUser.setBlockedAt(LocalDateTime.of(2022, 3, 1, 0, 0));


            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailAndIdNot(request.getEmail(), userId)).thenReturn(false);

            // When
            User updatedUser = userService.update(userId, request);

            // Then
            assertEquals(request.getEmail(), updatedUser.getEmail());
            assertEquals(request.getGsm(), updatedUser.getGsm());
            assertEquals(request.getTitle(), updatedUser.getTitle());
            assertEquals(request.getName(), updatedUser.getName());
            assertEquals(request.getLastName(), updatedUser.getLastName());
        }

        @Test
        void givenIdAndUpdateUserRequest_whenEmailAlreadyExists_thenThrowBadRequestException() {
            // Given
            String userId = "validUserId";
            UpdateUserRequest request = Factory.createUpdateUserRequest();
            request.setEmail("newemail@example.com");

            List<String> roles = List.of("ADMIN", "USER");
            request.setRoles(roles);

            User existingUser = Factory.createUser();
            existingUser.setId(userId);
            existingUser.setEmail("oldemail@example.com");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailAndIdNot(request.getEmail(), userId)).thenReturn(true);
            when(messageSourceService.get("unique_email")).thenReturn("unique_email");

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                userService.update(userId, request);
            });

            assertEquals("unique_email", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));

        }
    }

    @Nested
    @DisplayName("Test class for updatePassword scenarios")
    class UpdatePasswordTest {
        @Test
        void givenIdAndUpdatePasswordRequest_whenUpdatePassword_thenAssertBody() {
            // Given
            UpdatePasswordRequest request = Factory.createUpdatePasswordRequest();

            User existingUser = Factory.createUser();
            existingUser.setPassword(passwordEncoder.encode(request.getOldPassword()));

            when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())).thenReturn(true);
            when(userRepository.save(existingUser)).thenReturn(existingUser);

            // When
            userService.updatePassword(existingUser.getId(), request);

            // Then
            assertFalse(passwordEncoder.matches(request.getPassword(), existingUser.getPassword()));
            assertTrue(passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword()));
            verify(userRepository, times(1)).save(existingUser);
        }

        @Test
        void givenIdAndUpdatePasswordRequest_whenUpdatePasswordWithOldPasswordInCorrect_thenBadRequestException() {
            // Given
            UpdatePasswordRequest request = Factory.createUpdatePasswordRequest();

            User existingUser = Factory.createUser();
            existingUser.setPassword(passwordEncoder.encode("correctOldPassword"));

            when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())).thenReturn(false);
            when(messageSourceService.get("old_password_is_incorrect")).thenReturn("Old password is incorrect");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.updatePassword(existingUser.getId(), request));
            assertEquals("Old password is incorrect", exception.getMessage());
            verify(userRepository, never()).save(existingUser);
        }

        @Test
        void givenIdAndUpdatePasswordRequest_whenUpdatePasswordWithNewPasswordDifferentOldPassword_thenBadRequestException() {
            // Given
            String userId = "123";
            String oldPassword = "password123";
            String newPassword = "password123";

            UpdatePasswordRequest request = Factory.createUpdatePasswordRequest();
            request.setOldPassword(oldPassword);
            request.setPassword(newPassword);

            User existingUser = Factory.createUser();
            existingUser.setId(userId);
            existingUser.setPassword(passwordEncoder.encode(request.getOldPassword()));

            when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())).thenReturn(true);
            when(messageSourceService.get("new_password_must_be_different_from_old")).thenReturn("New password must be different from the old one");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.updatePassword(existingUser.getId(), request));
            assertEquals("New password must be different from the old one", exception.getMessage());
            verify(userRepository, never()).save(existingUser);
        }
    }

    @Nested
    @DisplayName("Test class for changePassword scenarios")
    class ChangePasswordTest {
        @Test
        void givenChangePasswordRequestAndToken_whenChangePassword_thenAssertBody() {
            // Given
            ChangePasswordRequest request = Factory.createChangePasswordRequest();
            request.setEmail("test@example.com");
            request.setPassword("newPassword");
            request.setPasswordConfirmation("newPassword");
            String token = "validToken";

            PasswordResetToken passwordResetToken = Factory.createPasswordResetToken();
            passwordResetToken.setUserId("userId");

            User user = Factory.createUser();
            user.setId("userId");
            user.setPassword("oldPassword");

            when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
            when(userRepository.save(user)).thenReturn(user);

            // When
            userService.changePassword(request, token);

            // Then
            verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);
            verify(emailService, times(1)).sendChangePasswordSuccess(user);
            assertEquals("encodedNewPassword", user.getPassword());
        }

        @Test
        void givenChangePasswordRequestAndToken_whenChangePasswordWithPasswordResetTokenExpired_thenBadRequestException() {
            // Given
            String token = "expiredToken";
            ChangePasswordRequest request = Factory.createChangePasswordRequest();
            User user = Factory.createUser();
            PasswordResetToken passwordResetToken = Factory.createPasswordResetToken();

            when(passwordResetTokenRepository.findByToken(token)).thenReturn(null);
            when(messageSourceService.get("password_reset_token_expired", new String[]{token})).thenReturn("Test");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.changePassword(request, token));
            assertEquals("Test", exception.getMessage());
        }

        @Test
        void givenChangePasswordRequestAndToken_whenChangePasswordWithInvalidTokenForMail_thenBadRequestException() {
            // Given
            String token = "invalidToken";
            ChangePasswordRequest request = Factory.createChangePasswordRequest();
            User user = Factory.createUser();
            PasswordResetToken passwordResetToken = Factory.createPasswordResetToken();

            when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(messageSourceService.get("invalid_token_for_mail")).thenReturn("Test");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.changePassword(request, token));
            assertEquals("Test", exception.getMessage());
        }

        @Test
        void givenChangePasswordRequestAndToken_whenChangePasswordWithPasswordMisMatch_thenBadRequestException() {
            // Given
            String token = "validToken";
            ChangePasswordRequest request = Factory.createChangePasswordRequest();

            PasswordResetToken passwordResetToken = Factory.createPasswordResetToken();
            passwordResetToken.setUserId("userId");

            User user = Factory.createUser();
            user.setId("userId");
            user.setPassword(passwordEncoder.encode("oldPassword"));

            when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(messageSourceService.get("password_mismatch")).thenReturn("TEST");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.changePassword(request, token));
            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenChangePasswordRequestAndToken_whenChangePasswordWithNewPasswordDifferentOldPassword_thenBadRequestException() {
            // Given
            ChangePasswordRequest request = Factory.createChangePasswordRequest();
            request.setEmail("test@example.com");
            request.setPassword("oldPassword");
            request.setPasswordConfirmation("oldPassword");
            String token = "validToken";

            PasswordResetToken passwordResetToken = Factory.createPasswordResetToken();
            passwordResetToken.setUserId("userId");

            User user = Factory.createUser();
            user.setId("userId");
            user.setPassword("oldPassword");

            when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(messageSourceService.get("new_password_must_be_different_from_old"))
                    .thenReturn("TEST");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.changePassword(request, token));
            assertEquals("TEST", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for assignUserToCompany scenarios")
    class AssignUserToCompanyTest {
        @Test
        void givenUserAndCompanyAndOwner_whenAssignUserToCompany_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();
            boolean owner = false;
            when(userRepository.save(user)).thenReturn(user);

            // When
            User result = userService.assignUserToCompany(user, company, owner);


            // Then
            assertFalse(user.getEmployees().isEmpty());
            assertTrue(user.getEmployees().stream().anyMatch(e -> e.getCompany().equals(company) && e.isOwner() == owner)); // Kullanıcı şirkette sahip mi?
            verify(userRepository, times(1)).save(user);
            assertSame(user, result);
        }

        @Test
        void givenUserAndCompanyAndOwner_whenAssignUserToCompanyWithUserAlreadyInTheCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();
            boolean owner = false;

            user.getEmployees().add(Employee.builder().company(company).build());
            when(messageSourceService.get("user_already_in_the_company")).thenReturn("User already in the company");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.assignUserToCompany(user, company, owner));
            assertEquals("User already in the company", exception.getMessage());
            verify(userRepository, never()).save(user);
        }

        @Test
        void givenUserAndCompanyAndOwner_whenAssignUserToCompanyWithCompanyHasOwner_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();
            boolean owner = true;

            company.getEmployees().add(Employee.builder().isOwner(true).build());
            when(messageSourceService.get("company_has_owner")).thenReturn("TEST");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.assignUserToCompany(user, company, owner));
            assertEquals("TEST", exception.getMessage());
            verify(userRepository, never()).save(user);
        }

    }

    @Nested
    @DisplayName("Test class for unAssignUserFromCompany scenarios")
    class UnAssignUserFromCompanyTest {
        @Test
        void givenUserAndCompany_whenUnAssignUserFromCompany_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();

            Employee employee = Employee.builder().company(company).build();
            user.getEmployees().add(employee);
            when(userRepository.save(user)).thenReturn(user);

            // When
            User result = userService.unAssignUserFromCompany(user, company);

            // Then
            assertFalse(user.getEmployees().contains(employee));
            verify(userRepository, times(1)).save(user);
            assertSame(user, result);
        }

        @Test
        void givenUserAndCompany_whenUnAssignUserFromCompanyWithUserNotInTheCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();

            when(messageSourceService.get("user_not_in_the_company")).thenReturn("User not in the company");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.unAssignUserFromCompany(user, company));
            assertEquals("User not in the company", exception.getMessage());
            verify(userRepository, never()).save(user);
        }
    }

    @Nested
    @DisplayName("Test class for updateUserOwnerStatus scenarios")
    class UpdateUserOwnerStatusTest {
        @Test
        void givenUserAndCompanyAndOwner_whenUpdateUserOwnerStatus_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();
            boolean owner = true;

            Employee employee = Factory.createEmployee();
            employee.setUser(user);
            employee.setCompany(company);
            employee.setOwner(false);
            user.getEmployees().add(employee);
            company.setEmployees(List.of(employee));

            when(userRepository.save(user)).thenReturn(user);

            // When and Then
            assertDoesNotThrow(() -> userService.updateUserOwnerStatus(user, company, owner));
            assertTrue(employee.isOwner());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void givenUserAndCompanyAndOwner_whenUpdateUserOwnerStatusWithCompanyHasOwner_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();
            boolean owner = true;

            company.getEmployees().add(Employee.builder().isOwner(true).build());
            when(messageSourceService.get("company_has_owner")).thenReturn("Test");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> userService.updateUserOwnerStatus(user, company, owner));

            assertEquals("Test", exception.getMessage());
            verify(userRepository, never()).save(user);
        }

        @Test
        void givenUserAndCompanyAndOwner_whenUpdateUserOwnerStatusWithUserNotInCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Company company = Factory.createCompany();
            boolean owner = false;

            when(messageSourceService.get("user_not_in_the_company")).thenReturn("Test");

            // When and Then
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> userService.updateUserOwnerStatus(user, company, owner));
            assertEquals("Test", exception.getMessage());
            verify(userRepository, never()).save(user);
        }
    }

    @Nested
    @DisplayName("Test class for passwordReset scenarios")
    class PasswordResetTest {
        @Test
        void givenResetPasswordRequest_whenPasswordReset_thenAssertBody() {
            // Given
            String email = "test@example.com";
            User user = Factory.createUser();
            user.setEmail(email);
            user.setId("userId");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserId(user.getId())).thenReturn(null);

            PasswordResetToken token = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString()) // Token will be generated dynamically
                    .userId(user.getId())
                    .build();
            when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);
            doNothing().when(emailService).sendResetPasswordEmail(any(User.class), anyString());

            // When
            User updatedUser = userService.passwordReset(email);

            // Then
            assertNotNull(updatedUser);
            assertTrue(updatedUser.getPasswordResetToken().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")); // Check if token is in valid UUID format
            verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class)); // Token should be saved
            verify(emailService, times(1)).sendResetPasswordEmail(user, updatedUser.getPasswordResetToken()); // Email should be sent with the generated token
        }

        @Test
        void givenResetPasswordRequest_whenPasswordReset_thenBadRequestException() {
            // Given
            String email = "test@example.com";
            User user = Factory.createUser();
            user.setEmail(email);
            user.setId("userId");

            PasswordResetToken existingToken = PasswordResetToken.builder()
                    .token("existingToken")
                    .userId(user.getId())
                    .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordResetTokenRepository.findByUserId(user.getId())).thenReturn(existingToken);
            when(messageSourceService.get("password_reset_token_exist")).thenReturn("password_reset_token_exist");


            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                userService.passwordReset(email);
            });

            assertEquals("password_reset_token_exist", exception.getMessage());
            verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
            verify(emailService, never()).sendResetPasswordEmail(any(User.class), anyString());
        }
    }

    @Nested
    @DisplayName("Test class for deleteById scenarios")
    class DeleteByIdTest {
        @Test
        void givenId_whenDeleteById_thenAssertBody() {
            // Given
            String validUserId = "validUserId";
            User userToDelete = Factory.createUser();
            when(userRepository.findById(validUserId)).thenReturn(Optional.of(userToDelete));

            // When
            userService.delete(validUserId);

            // Then
            verify(userRepository, times(1)).delete(userToDelete);
        }
    }

    @Nested
    @DisplayName("Test class for deleteByUser scenarios")
    class DeleteByUserTest {
        @Test
        void givenId_whenDeleteByUser_thenAssertBody() {
            // Given
            User userToDelete = Factory.createUser();

            // When
            userService.delete(userToDelete);

            // Then
            verify(userRepository, times(1)).delete(userToDelete);
        }
    }
}
