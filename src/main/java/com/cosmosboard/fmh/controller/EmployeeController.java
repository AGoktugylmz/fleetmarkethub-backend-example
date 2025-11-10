package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.employee.EmployeeRegisterRequest;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.employee.EmployeePaginationResponse;
import com.cosmosboard.fmh.dto.response.employee.EmployeeResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.service.EmployeeService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.RoleService;
import com.cosmosboard.fmh.service.UserInviteService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/employees")
@Authorize(roles = {"CONSULTANT"}, isOwer = true)
@Tag(name = "Employee", description = "Employee API")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController extends BaseController {

    private static final long TIME_TO_LIVE_FOR_INVITE = 30L; // 30 minutes

    private final UserService userService;

    private final EmployeeService employeeService;

    private final MessageSourceService messageSourceService;

    private final RoleService roleService;

    private final UserInviteService userInviteService;

    @Authorize(roles = {"CONSULTANT"}, isOwer = false)
    @GetMapping
    @Operation(summary = "Employee List Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Users List",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeePaginationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public EmployeePaginationResponse list(
        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) final Integer page,
        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) final Integer size,
        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) final String sortBy,
        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}, defaultValue = "asc"))
        @RequestParam(defaultValue = "asc", required = false) @Pattern(regexp = "asc|desc") final String sort
    ) {
        User getUser = getUser();
        Employee employee = getEmployeeOfUser(getUser);
        Page<Employee> users = employeeService.findAllByCompany(employee.getCompany(),
                PaginationCriteria.builder().page(page).size(size).sortBy(sortBy).sort(sort).columns(new String[]{}).build());
        return new EmployeePaginationResponse(users,
                users.stream().map(u -> EmployeeResponse.convert(u, false)).toList());
    }

    @PostMapping
    @Operation(summary = "Create Employee Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Send Invitation to Existed User",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "201", description = "Create User",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation Failed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to employee create", required = true)
        @RequestBody @Validated final EmployeeRegisterRequest request
    ) {
        User getUser = getUser();
        String email = request.getEmail();
        Employee employee = getOwnerEmployeeOfUser(getUser);
        if (!userService.existsByEmail(email)) {
            log.info("User email: {} not found in db, continue to create employee.", email);
            User user = userService.createEmployeeByOwner(request, List.of(roleService.findOneByName(AppConstants.RoleEnum.CONSULTANT)));
            employee = employeeService.createEmployee(employee.getCompany(), false, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeResponse.convert(employee, true));
        }
        if (employee.getCompany().getEmployees().stream().anyMatch(e -> Objects.equals(e.getUser().getEmail(), email))) {
            String message = messageSourceService.get("user_already_in_the_company");
            log.error(message);
            throw new BadRequestException(message);
        }
        Optional<UserInvite> byUserFromAndUserTo = userInviteService.findOptionalUserFromAndUserTo(getUser.getId(), email);
        if (byUserFromAndUserTo.isPresent()) {
            String message = messageSourceService.get("userInvite_success");
            log.error(message);
            throw new BadRequestException(message);
        }
        log.info("User email: {} found in db, continue to invite user.", email);
        UserInvite userInvite = UserInvite.builder()
                .userFrom(getUser.getId())
                .userTo(email)
                .timeToLive(TIME_TO_LIVE_FOR_INVITE)
                .build();
        userInvite = userInviteService.save(userInvite);
        User oneByEmail = userService.findOneByEmail(email);
        userInviteService.publishInvitationEvent(oneByEmail, userInvite);
        log.info("userInvite created: {}", userInvite.toString());
        return ResponseEntity.ok(SuccessResponse.builder().message(messageSourceService.get("userInvite_success")).build());
    }

    @PostMapping("/invite/{id}")
    @Operation(summary = "Accept Invitation to Join Company Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Accept Invitation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "NOT FOUND",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation Failed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Authorize(roles = {"CONSULTANT"})
    public EmployeeResponse acceptInvitation(
        @Parameter(name = "id", description = "Invite ID", required = true) @PathVariable("id") final String id
    ) {
        User getUser = getUser();
        UserInvite byId = userInviteService.findById(id);
        if (!Objects.equals(byId.getUserTo(), getUser.getEmail())) {
            String message = messageSourceService.get("userInvite_not_authorized", new String[]{id, getUser.getId()});
            log.error(message);
            throw new BadRequestException(message);
        }
        User getUserFrom = userService.findOneById(byId.getUserFrom());
        Employee byUserAndIsOwner = employeeService.findByUserAndIsOwner(getUserFrom, true);
        Company company = byUserAndIsOwner.getCompany();
        if (company.getEmployees().stream().anyMatch(e -> Objects.equals(e.getUser().getEmail(), getUser.getEmail()))) {
            String message = messageSourceService.get("user_already_in_the_company");
            log.error(message);
            throw new BadRequestException(message);
        }
        Employee newEmployee = Employee.builder()
                .user(getUser)
                .company(company)
                .isOwner(false)
                .build();
        newEmployee = employeeService.save(newEmployee);
        userInviteService.delete(byId);
        return EmployeeResponse.convert(newEmployee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove Employee From Company Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Employee removed successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation Failed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse> removeEmployeeFromCompany(
        @Parameter(name = "id", description = "User ID", required = true) @PathVariable("id") final String id
    ) {
        User getUser = getUser();
        Employee employeeToken = getOwnerEmployeeOfUser(getUser);

        User oneById = userService.findOneById(id);
        Employee employeeUrl = employeeService.findByUserAndIsOwner(oneById, false);

        if (employeeToken.getCompany() != employeeUrl.getCompany()) {
            String message = messageSourceService.get("are_not_allowed_access_employee_another_company");
            log.error(message);
            throw new AccessDeniedException(message);
        }

        employeeService.delete(employeeUrl);
        userService.delete(oneById);

        SuccessResponse response = SuccessResponse.builder()
            .message(String.format("Employee with ID %s has been successfully removed from the company.", id))
            .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get current user
     *
     * @return User entity retrieved from db
     */
    private User getUser() {
        return userService.getUser();
    }

    /**
     * Returns the owner employee associated with the given User object.
     *
     * @param getUser The User object for which the owner employee is to be retrieved.
     * @return The owner Employee object associated with the given User object.
     * @throws BadRequestException If the user is not associated with an owner employee or the employee does not have owner role.
     */
    private Employee getOwnerEmployeeOfUser(User getUser) {
        Optional<Employee> employeeOptional = employeeService.existByUserAndIsOwner(getUser, true);
        if (employeeOptional.isPresent()) {
            if (!employeeOptional.get().isOwner()) {
                String message = messageSourceService.get("employee_not_created_role_not_owner");
                log.error(message);
                throw new BadRequestException(message);
            }
            return employeeOptional.get();
        }
        String message = messageSourceService.get("employee_not_created_without_company");
        log.error(message);
        throw new BadRequestException(message);
    }

    private Employee getEmployeeOfUser(User user) {
        Optional<Employee> employeeOptional = employeeService.existByUser(user);
        if (employeeOptional.isPresent()) {
            return employeeOptional.get();
        }
        String message = messageSourceService.get("employee_not_created_without_company");
        log.error(message);
        throw new BadRequestException(message);
    }
}
