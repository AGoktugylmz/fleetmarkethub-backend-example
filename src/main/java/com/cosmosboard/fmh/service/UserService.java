package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.auth.RegisterRequest;
import com.cosmosboard.fmh.dto.request.employee.EmployeeRegisterRequest;
import com.cosmosboard.fmh.dto.request.user.ChangePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.CreateUserRequest;
import com.cosmosboard.fmh.dto.request.user.UpdatePasswordRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateProfileRequest;
import com.cosmosboard.fmh.dto.request.user.UpdateUserRequest;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.EmailActivationToken;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.GsmActivationToken;
import com.cosmosboard.fmh.entity.PasswordResetToken;
import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.UserFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.UserCriteria;
import com.cosmosboard.fmh.event.UserEmailActivationSendEvent;
import com.cosmosboard.fmh.event.UserGsmActivationSendEvent;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.ExpectationException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.repository.redis.PasswordResetTokenRepository;
import com.cosmosboard.fmh.repository.jpa.UserRepository;
import com.cosmosboard.fmh.security.JwtUserDetails;
import com.cosmosboard.fmh.service.storage.StorageService;
import com.cosmosboard.fmh.service.storage.StorageThumbnailService;
import com.cosmosboard.fmh.util.AppConstants;
import com.cosmosboard.fmh.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import static com.cosmosboard.fmh.util.AppConstants.AVATARS_PATH;
import static com.cosmosboard.fmh.util.AppConstants.AVATAR_SIZE;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final String USER_NOT_FOUND = "user_not_found";

    private static final String NEW_PASSWORD_MUST_BE_DIFFERENT = "new_password_must_be_different_from_old";

    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private final MessageSourceService messageSourceService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final EmailActivationTokenService emailActivationTokenService;

    private final GsmActivationTokenService gsmActivationTokenService;

    private final StorageService storageService;

    private final StorageThumbnailService storageThumbnailService;

    private final ApplicationEventPublisher eventPublisher;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final EmailService emailService;

    /**
     * Load user details by username.
     *
     * @param username String
     * @return UserDetails
     * @throws UsernameNotFoundException username didn't find exception.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(USER_NOT_FOUND)));
        return JwtUserDetails.create(user);
    }

    /**
     * Loads user details by ID.
     *
     * @param id String
     * @return UserDetails
     */
    @Transactional
    public UserDetails loadUserById(String id) {
        return JwtUserDetails.create(findOneById(id));
    }

    /**
     * Get authentication.
     *
     * @return Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Return the authenticated user.
     *
     * @return User entity
     */
    public User getUser() {
        Authentication authentication = getAuthentication();
        if (authentication.isAuthenticated()) {
            try {
                return findOneById(getPrincipal(authentication).getId());
            } catch (ClassCastException e) {
                log.warn("[ClassCastException] User details not found!");
            } catch (NotFoundException e) {
                log.warn("[NotFoundException] User not found!");
            }
        }
        return null;
    }

    /**
     * Get the first company of the authenticated user.
     *
     * @return Company entity
     */
    public Company getCompany() {
        try {
            return getUser().getEmployees().get(0).getCompany();
        } catch (IndexOutOfBoundsException e) {
            log.warn("[IndexOutOfBoundsException] Employee not found!: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get UserDetails from security context
     *
     * @param authentication Wrapper for security context
     * @return the Principal being authenticated or the authenticated principal after authentication.
     */
    public JwtUserDetails getPrincipal(Authentication authentication) {
        return (JwtUserDetails) authentication.getPrincipal();
    }

    /**
     * Count all users.
     *
     * @return long
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * Find all users with pagination.
     *
     * @param userCriteria       UserCriteria
     * @param paginationCriteria PaginationCriteria
     * @return Page of user
     */
    public Page<User> findAll(UserCriteria userCriteria, PaginationCriteria paginationCriteria) {
        return userRepository.findAll(new UserFilterSpecification(userCriteria),
            PageRequestBuilder.build(paginationCriteria));
    }

    /**
     * Find a user by ID.
     *
     * @param id String
     * @return User
     */
    public User findOneById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(USER_NOT_FOUND)));
    }

    /**
     * Find a user by e-mail.
     *
     * @param email String.
     * @return User
     */
    public User findOneByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(USER_NOT_FOUND)));
    }

    /**
     * Find a user by gsm.
     *
     * @param gsm String.
     * @return User
     */
    public User findOneByGsm(String gsm) {
        return userRepository.findByGsm(gsm)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(USER_NOT_FOUND)));
    }

    /**
     * Checks if a user with the given email address exists in the repository.
     *
     * @param email String.
     * @return boolean.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Create user from request.
     *
     * @param request CreateUserRequest
     * @return User
     */
    public User create(CreateUserRequest request) {
        User user = buildUserFromRequest(request);
        user.setRoles(getRolesFromList(request.getRoles()));

        if (request.getIsEmailActivated() != null && request.getIsEmailActivated()) {
            user.setEmailActivatedAt(LocalDateTime.now());
        }

        if (request.getIsGsmActivated() != null && request.getIsGsmActivated()) {
            user.setGsmActivatedAt(LocalDateTime.now());
        }

        if (request.getIsBlocked() != null && request.getIsBlocked()) {
            user.setBlockedAt(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    /**
     * Creates a new User object based on the RegisterRequest object, associates it with the USER role, saves it to the user repository,
     * sends an email activation event, and returns the newly created User object.
     *
     * @param request The RegisterRequest object containing the details for the new User object.
     * @return The newly created User object.
     */
    public User createEmployee(RegisterRequest request) {
        return createEmployee(request, List.of(roleService.findOneByName(AppConstants.RoleEnum.USER)));
    }

    /**
     * Creates a new User object based on the RegisterRequest object, associates it with the given roles,
     * saves it to the user repository, sends an email activation event, and returns the newly created User object.
     *
     * @param request The RegisterRequest object containing the details for the new User object.
     * @return The newly created User object.
     */
    public User createEmployee(RegisterRequest request, List<Role> roles) {
        User user = buildUserFromRequest(request);
        user.setRoles(roles);

        user = userRepository.save(user);
        EmailActivationToken emailActivationToken = generateEmailActivationToken(user);
        user.setEmailActivationToken(emailActivationToken.getToken());

        //      emailService.sendUserEmailActivation(user);

        GsmActivationToken gsmActivationToken = gsmActivationEventPublisher(user);
        user.setGsmActivationToken(gsmActivationToken.getToken());
        return user;
    }

    /**
     * Creates a new employee by the company owner and assigns roles and activation tokens.
     * This method builds a user from the provided employee registration request,
     * assigns the specified roles, saves the user to the repository,
     * generates email and GSM activation tokens, and sets them on the user.
     *
     * @param request The employee registration request containing the necessary user details.
     * @param roles A list of roles to assign to the new user.
     * @return The newly created user with roles and activation tokens.
     */
    public User createEmployeeByOwner(EmployeeRegisterRequest request, List<Role> roles) {
        User user = buildUserFromRequestByOwner(request);
        user.setRoles(roles);

        //        EmailActivationToken emailActivationToken = emailActivationEventPublisher(user);
        //        user.setEmailActivationToken(emailActivationToken.getToken());
        //
        //        GsmActivationToken gsmActivationToken = gsmActivationEventPublisher(user);
        //        user.setGsmActivationToken(gsmActivationToken.getToken());

        user.setEmailActivatedAt(LocalDateTime.now());
        user.setGsmActivatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return user;
    }

    /**
     * Resend e-mail activation token by e-mail.
     *
     * @param email String
     * @return User
     */
    public User resendEmailActivationByEmail(String email) {
        User user = findOneByEmail(email);
        resendEmailActivation(user);
        return user;
    }

    /**
     * Resend e-mail activation token by ID.
     *
     * @param id String
     */
    public void resendEmailActivationById(String id) {
        User user = findOneById(id);
        resendEmailActivation(user);
    }

    /**
     * Resend GSM activation token by gsm.
     *
     * @param email String
     * @param gsm   String
     * @return User
     */
    public User resendGsmActivationByGsm(String email, String gsm) {
        User user = findOneByEmail(email);
        if (!Objects.equals(user.getGsm(), gsm)) {
            log.error("User {} does not belong to gsm activation", user.getEmail());
            throw new NotFoundException(messageSourceService.get("user_not_found"));
        }
        resendGsmActivation(user);
        return user;
    }

    /**
     * Resend GSM activation token by ID.
     *
     * @param id String
     */
    public void resendGsmActivationById(String id) {
        User user = findOneByEmail(id);
        resendGsmActivation(user);
    }

    /**
     * Activate user's email by token.
     *
     * @param token String
     */
    public void activateEmail(String token) {
        EmailActivationToken emailActivationToken = emailActivationTokenService.getUserByToken(token);
        emailActivationTokenService.delete(emailActivationToken);
        User user = findOneById(emailActivationToken.getUserId());
        user.setEmailActivatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Activate user's GSM number.
     *
     * @param userId String
     * @param token  String
     */
    public void activateGsm(String userId, String token) {
        GsmActivationToken gsmActivationToken = gsmActivationTokenService.getUserByUserIdAndToken(userId, token);
        User user = findOneById(userId);
        if (!Objects.equals(gsmActivationToken.getUserId(), user.getId())) {
            log.error("User with id {} does not belong to user with id {}", user.getId(), userId);
            throw new NotFoundException("User not found");
        }
        user.setGsmActivatedAt(LocalDateTime.now());
        userRepository.save(user);
        gsmActivationTokenService.delete(gsmActivationToken);
    }

    /**
     * Update user's profile.
     *
     * @param id      String
     * @param request UpdateProfileRequest
     * @return User
     */
    public User updateProfile(String id, UpdateProfileRequest request) {
        User user = findOneById(id);

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!user.getEmail().equals(request.getEmail())) {
                if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                    log.error("[User profile update] Email already exists!");
                    throw new BadRequestException(messageSourceService.get("unique_email"));
                }
                user.setEmail(request.getEmail());
                user.setEmailActivatedAt(null);
            }
        }

        if (request.getGsm() != null && !request.getGsm().isEmpty()) {
            if (user.getGsm() == null || !user.getGsm().equals(request.getGsm())) {
                user.setGsm(request.getGsm());
                user.setGsmActivatedAt(null);
            }
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            user.setTitle(request.getTitle());
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
        }

        boolean isOldPasswordPresent = request.getOldPassword() != null && !request.getOldPassword().isEmpty();
        boolean isNewPasswordPresent = request.getPassword() != null && !request.getPassword().isEmpty();

        if (isOldPasswordPresent != isNewPasswordPresent) {
            String msg = messageSourceService.get("both_password_fields_required");
            log.error(msg);
            throw new BadRequestException(msg);
        }

        if (isOldPasswordPresent) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                String msg = messageSourceService.get("old_password_is_incorrect");
                log.error(msg);
                throw new BadRequestException(msg);
            }

            if (request.getOldPassword().equals(request.getPassword())) {
                String msg = messageSourceService.get(NEW_PASSWORD_MUST_BE_DIFFERENT);
                log.error(msg);
                throw new BadRequestException(msg);
            }

            if (request.getPasswordConfirm() == null || !request.getPassword().equals(request.getPasswordConfirm())) {
                String msg = messageSourceService.get("password_confirm_must_match");
                log.error(msg);
                throw new BadRequestException(msg);
            }

            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            emailActivationEventPublisher(user);
        }

        if (request.getGsm() != null && !request.getGsm().equals(user.getGsm())) {
            gsmActivationEventPublisher(user);
        }

        return user;
    }

    /**
     * Update avatar by id from request.
     *
     * @param id     String
     * @param avatar MultipartFile
     * @return User
     */
    public User updateAvatar(String id, MultipartFile avatar) {
        User user = findOneById(id);

        String filename = storageService.store(avatar, AVATARS_PATH, true);
        String path = Paths.get(AVATARS_PATH, filename).toString();

        try {
            storageThumbnailService.make(path)
                .resize(AVATAR_SIZE, AVATAR_SIZE)
                .save(path);
        } catch (ExpectationException e) {
            log.warn("Error while creating thumbnail: {}", e.getMessage());
        }
        deleteAvatarFromStorage(user);

        user.setAvatar(filename);
        userRepository.save(user);

        return user;
    }

    /**
     * Delete avatar from user ID.
     *
     * @param id String
     */
    public void deleteAvatar(String id) {
        User user = findOneById(id);

        if (user.getAvatar() == null) {
            log.error("Avatar not found for user name {}", user.getName());
            throw new NotFoundException(messageSourceService.get("avatar_not_found"));
        }

        deleteAvatarFromStorage(user);

        user.setAvatar(null);
        userRepository.save(user);
    }

    /**
     * Update user by ID from request.
     *
     * @param id      String
     * @param request UpdateUserRequest
     * @return User
     */
    public User update(String id, UpdateUserRequest request) {
        User user = findOneById(id);
        boolean isEmailChanged = request.getEmail() != null && !user.getEmail().equals(request.getEmail());
        boolean isGsmChanged = (user.getGsm() != null && request.getGsm() != null &&
            !user.getGsm().equals(request.getGsm())) || (user.getGsm() == null && request.getGsm() != null);

        if (request.getRoles() != null) {
            user.setRoles(getRolesFromList(request.getRoles()));
        }

        if (isEmailChanged) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                String uniqueEmail = messageSourceService.get("unique_email");
                log.error("[User profile update] {}", uniqueEmail);
                throw new BadRequestException(uniqueEmail);
            }

            user.setEmail(request.getEmail());
            user.setEmailActivatedAt(null);
        }

        if (isGsmChanged) {
            if (request.getGsm().isEmpty()) {
                request.setGsm(null);
            }

            user.setGsm(request.getGsm());
            user.setGsmActivatedAt(null);
        }

        if (request.getTitle() != null) {
            if (request.getTitle().isEmpty()) {
                request.setTitle(null);
            }

            user.setTitle(request.getTitle());
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
        }

        if (request.getIsEmailActivated() != null) {
            if (request.getIsEmailActivated()) {
                user.setEmailActivatedAt(LocalDateTime.now());
            } else {
                user.setEmailActivatedAt(null);
            }
        }

        if (request.getIsGsmActivated() != null) {
            if (request.getIsGsmActivated()) {
                user.setGsmActivatedAt(LocalDateTime.now());
            } else {
                user.setGsmActivatedAt(null);
            }
        }

        if (request.getIsBlocked() != null && request.getIsBlocked()) {
            user.setBlockedAt(LocalDateTime.now());
        } else {
            user.setBlockedAt(null);
        }

        userRepository.save(user);

        return user;
    }

    /**
     * Update user password from request.
     *
     * @param id      String
     * @param request UpdatePasswordRequest
     */
    public void updatePassword(String id, UpdatePasswordRequest request) {
        User user = findOneById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            String msg = messageSourceService.get("old_password_is_incorrect");
            log.error(msg);
            throw new BadRequestException(msg);
        }

        if (request.getOldPassword().equals(request.getPassword())) {
            String msg = messageSourceService.get(NEW_PASSWORD_MUST_BE_DIFFERENT);
            log.error(msg);
            throw new BadRequestException(msg);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /**
     * Changes the user's password from request.
     *
     * @param request ChangePasswordRequest.
     * @param token   String.
     */
    public void changePassword(ChangePasswordRequest request, String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            String msg = messageSourceService.get("password_reset_token_expired", new String[]{token});
            log.error(msg);
            throw new BadRequestException(msg);
        }
        User user = findOneByEmail(request.getEmail());
        if (!passwordResetToken.getUserId().equals(user.getId())) {
            String msg = messageSourceService.get("invalid_token_for_mail");
            log.error(msg);
            throw new BadRequestException(msg);
        }
        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            String msg = messageSourceService.get("password_mismatch");
            log.error(msg);
            throw new BadRequestException(msg);
        }
        if (user.getPassword().equals(request.getPassword())) {
            String msg = messageSourceService.get(NEW_PASSWORD_MUST_BE_DIFFERENT);
            log.error(msg);
            throw new BadRequestException(msg);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
        emailService.sendChangePasswordSuccess(user);
    }

    /**
     * Assign User to Company
     *
     * @param user    User
     * @param company Company
     * @param owner   isOwner
     */
    public User assignUserToCompany(User user, Company company, boolean owner) {
        boolean isExistingOwner = company.getEmployees().stream().anyMatch(Employee::isOwner);

        if (user.getEmployees().stream().anyMatch(c -> c.getCompany().equals(company))) {
            String message = messageSourceService.get("user_already_in_the_company");
            log.error(message);
            throw new BadRequestException(message);
        }

        if (isExistingOwner && owner) {
            String message = messageSourceService.get("company_has_owner");
            log.error(message);
            throw new BadRequestException(message);
        }

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setCompany(company);
        employee.setOwner(owner);
        user.getEmployees().add(employee);
        Role consultantRole = roleService.findOneByName(AppConstants.RoleEnum.CONSULTANT);
        if (!user.getRoles().contains(consultantRole)) {
            user.getRoles().add(consultantRole);
        }
        return userRepository.save(user);
    }

    /**
     * UnAssign User to Company
     *
     * @param user    User
     * @param company Company
     */
    public User unAssignUserFromCompany(User user, Company company) {
        if (user.getEmployees().stream().noneMatch(c -> c.getCompany().equals(company))) {
            String message = messageSourceService.get("user_not_in_the_company");
            log.error(message);
            throw new BadRequestException(message);
        }

        Employee employeeToRemove = user.getEmployees().stream()
            .filter(employee -> employee.getCompany().equals(company))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("employee_not_found"));

        user.getEmployees().remove(employeeToRemove);
        if (user.getEmployees().isEmpty()) {
            Role consultantRole = roleService.findOneByName(AppConstants.RoleEnum.CONSULTANT);
            user.getRoles().remove(consultantRole);
        }
        return userRepository.save(user);
    }

    /**
     * Update Employee isOwner
     *
     * @param user    User
     * @param company Company
     * @param owner   isOwner
     */
    public User updateUserOwnerStatus(User user, Company company, boolean owner) {
        boolean isExistingOwner = company.getEmployees().stream().anyMatch(Employee::isOwner);

        if (isExistingOwner && owner) {
            String message = messageSourceService.get("company_has_owner");
            log.error(message);
            throw new BadRequestException(message);
        }

        Optional<Employee> existingEmployee = user.getEmployees().stream()
            .filter(employee -> employee.getCompany().equals(company))
            .findFirst();
        if (existingEmployee.isEmpty()) {
            String message = messageSourceService.get("user_not_in_the_company");
            log.error(message);
            throw new BadRequestException(message);
        }
        Employee employee = existingEmployee.get();
        employee.setOwner(owner);
        return userRepository.save(user);
    }

    /**
     * PasswordReset user passwordReset from request.
     *
     * @param email String email.
     * @return User
     */
    public User passwordReset(final String email) {
        User user = findOneByEmail(email);
        PasswordResetToken existingToken = passwordResetTokenRepository.findByUserId(user.getId());
        if (existingToken != null) {
            passwordResetTokenRepository.delete(existingToken);
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
            .token(token)
            .userId(user.getId())
            .build();
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendResetPasswordEmail(user, token);
        user.setPasswordResetToken(token);
        return user;
    }

    /**
     * Delete user by id.
     *
     * @param id String
     */
    public void delete(String id) {
        delete(findOneById(id));
    }

    /**
     * Deletes a user from the system.
     *
     * @param user The user to be deleted.
     */
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * Build a user object from RegisterRequest.
     *
     * @param request RegisterRequest
     */
    private User buildUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setEmailActivatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Build a user object from RegisterRequest.
     *
     * @param request EmployeeRegisterRequest
     */
    private User buildUserFromRequestByOwner(EmployeeRegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        return user;
    }

    /**
     * E-mail activation event publisher.
     *
     * @param user User
     * @return EmailActivationToken
     */
    private EmailActivationToken emailActivationEventPublisher(User user) {
        EmailActivationToken emailActivationToken = emailActivationTokenService.create(user);
        eventPublisher.publishEvent(new UserEmailActivationSendEvent(this, user));
        return emailActivationToken;
    }

    /**
     * Generates an email activation token for a given user.
     * This method uses the emailActivationTokenService to create an email activation token
     * for the specified user. The token is necessary for confirming the user's email address.
     *
     * @param user The user for whom the email activation token is to be generated.
     * @return The generated email activation token for the user.
     */
    private EmailActivationToken generateEmailActivationToken(User user) {
        return emailActivationTokenService.create(user);
    }

    /**
     * GSM activation event publisher.
     *
     * @param user User
     * @return GsmActivationToken
     */
    private GsmActivationToken gsmActivationEventPublisher(User user) {
        GsmActivationToken gsmActivationToken = gsmActivationTokenService.create(user);
        eventPublisher.publishEvent(new UserGsmActivationSendEvent(this, user));
        return gsmActivationToken;
    }

    /**
     * Resend e-mail activation link.
     *
     * @param user User
     */
    private void resendEmailActivation(User user) {
        if (user.getEmailActivatedAt() != null) {
            String msg = messageSourceService.get("this_email_already_activated");
            log.error(msg);
            throw new BadRequestException(msg);
        }

        emailActivationEventPublisher(user);
    }

    /**
     * Resend GSM activation code.
     *
     * @param user User
     */
    private void resendGsmActivation(User user) {
        if (user.getGsmActivatedAt() != null) {
            String msg = messageSourceService.get("this_gsm_already_activated");
            log.error(msg);
            throw new BadRequestException(msg);
        }

        gsmActivationEventPublisher(user);
    }

    /**
     * Delete avatar from storage.
     *
     * @param user User
     */
    private void deleteAvatarFromStorage(User user) {
        if (user.getAvatar() != null) {
            try {
                storageService.delete(Paths.get(AVATARS_PATH, user.getAvatar()).toString());
            } catch (Exception e) {
                log.warn("Error deleting old avatar: {}", e.getMessage());
            }
        }
    }

    /**
     * Get roles from a list of string.
     *
     * @param roles List of roles
     * @return List of roles
     */
    private List<Role> getRolesFromList(List<String> roles) {
        List<Role> roleSet = new ArrayList<>();
        for (String role : roles) {
            roleSet.add(roleService.findOneByName(AppConstants.RoleEnum.valueOf(role)));
        }
        return roleSet;
    }

    /**
     * Adds a car to the user's list of favorite cars.
     * This method checks if the car is already in the user's list of favorite cars.
     * If the car is not already in the list, it adds the car to the favorites and saves
     * the updated user object to the repository.
     *
     * @param userId The ID of the user to whom the car should be added to the favorites.
     * @param car The car to be added to the user's favorite cars list.
     */
    public void addCarToFavorites(String userId, Car car) {
        User user = findOneById(userId);
        if (!user.getFavoriteCars().contains(car)) {
            user.getFavoriteCars().add(car);
            userRepository.save(user);
        }
    }

    /**
     * Retrieves a paginated list of favorite cars for a specific user.
     * This method fetches the user's favorite cars based on the provided user ID and
     * pagination criteria. It uses the Spring Data `Pageable` interface to handle pagination
     * and sorting.
     *
     * @param userId The ID of the user whose favorite cars are being retrieved.
     * @param paginationCriteria The pagination and sorting criteria (page number, size, sort field).
     * @return A page of cars that are marked as favorites by the user.
     */
    public Page<Car> findFavoriteCars(String userId, PaginationCriteria paginationCriteria) {
        Pageable pageable = PageRequest.of(
            paginationCriteria.getPage() - 1,
            paginationCriteria.getSize(),
            Sort.by(Sort.Direction.fromString(paginationCriteria.getSort()), paginationCriteria.getSortBy())
        );
        return userRepository.findFavoriteCarsByUserId(userId, pageable);
    }

    /**
     * Removes a car from a user's list of favorite cars.
     * This method checks if the car is already in the user's favorites. If the car is not in
     * the favorites, a BadRequestException is thrown. Otherwise, the car is removed from the
     * favorite list, and the user's updated information is saved.
     *
     * @param userId The ID of the user from whose favorites the car will be removed.
     * @param car The car that will be removed from the user's favorites.
     * @throws BadRequestException If the car is not in the user's favorite list.
     */
    public void removeCarFromFavorites(String userId, Car car) {
        User user = findOneById(userId);
        if (!user.getFavoriteCars().contains(car)) {
            throw new BadRequestException("Car is not in favorites");
        }
        user.getFavoriteCars().remove(car);
        userRepository.save(user);
    }

    /**
     * Retrieves a list of user IDs who have favorite a specific car.
     * This method queries the repository to find all users who have added the specified car
     * to their list of favorites.
     *
     * @param carId The ID of the car for which we want to find the users who are a favorite it.
     * @return A list of user IDs who have favorite the specified car.
     */
    public List<String> findUserIdsWhoFavoriteCar(String carId) {
        return userRepository.findUserIdsByCarId(carId);
    }
}
