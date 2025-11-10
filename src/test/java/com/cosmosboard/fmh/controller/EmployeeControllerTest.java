package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.employee.EmployeeRegisterRequest;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.employee.EmployeePaginationResponse;
import com.cosmosboard.fmh.dto.response.employee.EmployeeResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.EmployeeService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.RoleService;
import com.cosmosboard.fmh.service.UserInviteService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for EmployeeController")
public class EmployeeControllerTest {
    @InjectMocks
    private EmployeeController employeeController;
    @Mock
    private UserService userService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private RoleService roleService;

    @Mock
    private UserInviteService userInviteService;

    @Nested
    @DisplayName("Test class for employee list scenarios")
    class ListTest {
        @Test
        void givenPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            Integer page = 1;
            Integer size = 20;
            String sortBy = "createdAt";
            String sort = "asc";

            User user = Factory.createUser();
            Company userCompany = Factory.createCompany();
            Employee employee = Factory.createEmployee();
            employee.setCompany(userCompany);
            employee.setOwner(true);
            List<Employee> employeeList = new ArrayList<>();
            Page<Employee> users = new PageImpl<>(employeeList);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true))
                    .thenReturn(Optional.of(employee));
            when(employeeService.findAllByCompany(employee.getCompany(),
                    PaginationCriteria.builder().page(page).size(size).sortBy(sortBy)
                            .sort(sort).columns(new String[]{}).build())).thenReturn(users);


            // When
            EmployeePaginationResponse response = employeeController.list(page, size, sortBy, sort);

            // Then
            assertNotNull(response);
        }

        @Test
        void givenPageAndSizeAndSortByAndSort_whenListWithEmployeeNotCreatedRoleNotOwner_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            employee.setOwner(false);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(messageSourceService.get("employee_not_created_role_not_owner")).thenReturn("test");

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.list(1, 20, "createdAt", "asc");
            });

            // Then
            assertEquals("test", exception.getMessage());
            verify(employeeService, times(1)).existByUserAndIsOwner(user, true);
        }

        @Test
        void givenPageAndSizeAndSortByAndSort_whenListWithEmployeeNotCreatedWithoutCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            employee.setOwner(false);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.empty());
            when(messageSourceService.get("employee_not_created_without_company")).thenReturn("test");

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.list(1, 20, "createdAt", "asc");
            });

            // Then
            assertEquals("test", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for employee create scenarios")
    class CreateTest {
        @Test
        void givenRegisterRequest_whenCreate_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            user.setEmail("test@example.com");
            Employee employee = Factory.createEmployee();
            employee.setOwner(true);
            EmployeeRegisterRequest request = Factory.createEmployeeRegisterRequest();
            request.setEmail("test@example.com");
            Role consultantRole = new Role();
            consultantRole.setName(AppConstants.RoleEnum.CONSULTANT);
            consultantRole.setId("roleId");

            // Mock'lar
            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(userService.existsByEmail(request.getEmail())).thenReturn(false);
            when(roleService.findOneByName(AppConstants.RoleEnum.CONSULTANT)).thenReturn(consultantRole);

            // createEmployee çağrısı için null yerine eşleştirici ekliyoruz
            when(userService.createEmployeeByOwner(eq(request), eq(List.of(consultantRole))))
                    .thenReturn(user);
            when(employeeService.createEmployee(employee.getCompany(), false, user)).thenReturn(employee);

            // When
            ResponseEntity<?> response = employeeController.create(request);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(employee.getId(), ((EmployeeResponse) response.getBody()).getId());
            assertTrue(((EmployeeResponse) response.getBody()).isOwner());
        }

        @Test
        void givenRegisterRequest_whenCreateWithEmployeeNotCreatedRoleNotOwner_thenBadRequestException() {
            // Given
            EmployeeRegisterRequest request = Factory.createEmployeeRegisterRequest();
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            employee.setOwner(false);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(messageSourceService.get("employee_not_created_role_not_owner")).thenReturn("test");

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.create(request);
            });

            // Then
            assertEquals("test", exception.getMessage());
            verify(employeeService, times(1)).existByUserAndIsOwner(user, true);
        }

        @Test
        void givenRegisterRequest_whenCreateWithEmployeeNotCreatedWithoutCompany_thenBadRequestException(){
            // Given
            EmployeeRegisterRequest request = Factory.createEmployeeRegisterRequest();
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            employee.setOwner(false);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.empty());
            when(messageSourceService.get("employee_not_created_without_company")).thenReturn("test");

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.create(request);
            });

            // Then
            assertEquals("test", exception.getMessage());
        }

        @Test
        void givenRegisterRequest_whenCreateWithUserAlreadyInCompany_thenBadRequestException() {
            // Given
            EmployeeRegisterRequest request = Factory.createEmployeeRegisterRequest();
            request.setEmail("test@example.com");

            User user = Factory.createUser();
            user.setEmail("test@example.com");
            Company company = Factory.createCompany();
            Employee employee = Factory.createEmployee();
            employee.setOwner(true);
            employee.setCompany(company);
            employee.setUser(user);
            company.getEmployees().add(employee);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(userService.existsByEmail(request.getEmail())).thenReturn(true);
            when(messageSourceService.get("user_already_in_the_company")).thenReturn("TEST");

            // When ve Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.create(request);
            });

            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenRegisterRequest_whenCreateWithUserInviteSuccess_thenBadRequestException() {
            // Given
            EmployeeRegisterRequest request = Factory.createEmployeeRegisterRequest();
            request.setEmail("test@example.com");

            User user = Factory.createUser();
            user.setEmail("test@example.com");
            Employee employee = Factory.createEmployee();
            employee.setOwner(true);
            employee.setUser(user);
            UserInvite userInvite = Factory.createUserInvite();

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(userService.existsByEmail(request.getEmail())).thenReturn(true);
            when(userInviteService.findOptionalUserFromAndUserTo(user.getId(), request.getEmail())).thenReturn(Optional.of(userInvite));
            when(messageSourceService.get("userInvite_success")).thenReturn("TEST");

            // When ve Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.create(request);
            });

            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenRegisterRequest_whenCreateWithUserInviteSuccess_thenAssertBody(){
            // Given
            EmployeeRegisterRequest request = Factory.createEmployeeRegisterRequest();
            request.setEmail("test@example.com");

            User user = Factory.createUser();
            user.setEmail("test@example.com");
            Employee employee = Factory.createEmployee();
            employee.setOwner(true);
            employee.setUser(user);
            UserInvite userInvite = Factory.createUserInvite();

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(userService.existsByEmail(request.getEmail())).thenReturn(true);
            when(userInviteService.findOptionalUserFromAndUserTo(user.getId(), request.getEmail())).thenReturn(Optional.empty());
            when(userInviteService.save(any(UserInvite.class))).thenReturn(userInvite);
            when(userService.findOneByEmail(request.getEmail())).thenReturn(Factory.createUser());
            when(messageSourceService.get("userInvite_success")).thenReturn("TEST");

            // When & Then
            ResponseEntity<?> response = employeeController.create(request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertInstanceOf(SuccessResponse.class, response.getBody());

            verify(userInviteService, times(1)).save(any(UserInvite.class));
        }
    }

    @Nested
    @DisplayName("Test class for employee acceptInvitation scenarios")
    class AcceptInvitationTest {
        @Test
        void givenId_whenAcceptInvitationWithNotAuthorized_thenBadRequestException() {
            // Given
            String id = "invitationId123";
            User getUser = Factory.createUser();
            UserInvite userInvite = Factory.createUserInvite();

            when(userService.getUser()).thenReturn(getUser);
            when(userInviteService.findById(id)).thenReturn(userInvite);
            when(messageSourceService.get("userInvite_not_authorized", new String[]{id, getUser.getId()})).thenReturn("Test");

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.acceptInvitation(id);
            });
            assertEquals("Test", exception.getMessage());
        }

        @Test
        void givenId_whenAcceptInvitationWithUserAlreadyInCompany_thenBadRequestException() {
            // Given
            String id = "invitationId123";
            String userToEmail = "user@example.com";

            User getUser = Factory.createUser();
            getUser.setEmail(userToEmail);

            UserInvite userInvite = Factory.createUserInvite();
            userInvite.setId(id);
            userInvite.setUserTo(userToEmail);

            User getUserFrom = Factory.createUser();
            Employee byUserAndIsOwner = Factory.createEmployee();
            byUserAndIsOwner.setOwner(true);
            byUserAndIsOwner.setUser(getUser);
            byUserAndIsOwner.getUser().setEmail(userToEmail);

            Company company = Factory.createCompany();
            company.getEmployees().add(byUserAndIsOwner);
            byUserAndIsOwner.setCompany(company);

            when(userService.getUser()).thenReturn(getUser);
            when(userInviteService.findById(id)).thenReturn(userInvite);
            when(messageSourceService.get("user_already_in_the_company")).thenReturn("Test");
            when(userService.findOneById(userInvite.getUserFrom())).thenReturn(getUserFrom);
            when(employeeService.findByUserAndIsOwner(getUserFrom, true)).thenReturn(byUserAndIsOwner);

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.acceptInvitation(id);
            });

            assertEquals("Test", exception.getMessage());
        }

        @Test
        void givenId_whenAcceptInvitation_thenAssertBody() {
            // Given
            String id = "invitationId123";
            String userToEmail = "user@example.com";

            User getUser = Factory.createUser();
            getUser.setEmail(userToEmail);

            UserInvite userInvite = Factory.createUserInvite();
            userInvite.setId(id);
            userInvite.setUserTo(userToEmail);

            User getUserFrom = Factory.createUser();
            Employee byUserAndIsOwner = Factory.createEmployee();
            byUserAndIsOwner.setOwner(true);
            byUserAndIsOwner.setUser(getUser);
            byUserAndIsOwner.getUser().setEmail(userToEmail);

            Employee newEmployee = Factory.createEmployee();
            newEmployee.setOwner(false);

            when(userService.getUser()).thenReturn(getUser);
            when(userInviteService.findById(id)).thenReturn(userInvite);
            when(userService.findOneById(userInvite.getUserFrom())).thenReturn(getUserFrom);
            when(employeeService.findByUserAndIsOwner(getUserFrom, true)).thenReturn(byUserAndIsOwner);
            when(employeeService.save(any(Employee.class))).thenReturn(newEmployee);

            // When
            EmployeeResponse response = employeeController.acceptInvitation(id);

            // Then
            assertNotNull(response);
            assertEquals(newEmployee.getId(), response.getId());
            assertFalse(newEmployee.isOwner());
        }
    }

    @Nested
    @DisplayName("Test class for employee removeEmployeeFromCompany scenarios")
    class RemoveEmployeeFromCompanyTest {
        @Test
        void givenId_whenRemoveEmployeeFromCompany_thenAssertBody() {
            // Given
            String userId = "userId123";
            User getUser = Factory.createUser();
            User oneById = Factory.createUser();
            Employee employeeToken = Factory.createEmployee();
            employeeToken.setOwner(true);
            Employee employeeUrl = Factory.createEmployee();
            employeeUrl.setCompany(employeeToken.getCompany());

            when(userService.getUser()).thenReturn(getUser);
            when(userService.findOneById(userId)).thenReturn(oneById);
            when(userService.getUser()).thenReturn(getUser);
            when(employeeService.existByUserAndIsOwner(getUser, true))
                    .thenReturn(Optional.of(employeeToken));
            when(employeeService.findByUserAndIsOwner(oneById, false)).thenReturn(employeeUrl);

            // When
            ResponseEntity<SuccessResponse> response = employeeController.removeEmployeeFromCompany(userId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Employee with ID userId123 has been successfully removed from the company.",
                    Objects.requireNonNull(response.getBody()).getMessage());
        }

        @Test
        void givenId_whenRemoveEmployeeFromCompanyWithEmployeeNotCreatedRoleNotOwner_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            employee.setOwner(false);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.of(employee));
            when(messageSourceService.get("employee_not_created_role_not_owner")).thenReturn("test");

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.removeEmployeeFromCompany(user.getId());
            });

            // Then
            assertEquals("test", exception.getMessage());
            verify(employeeService, times(1)).existByUserAndIsOwner(user, true);
        }

        @Test
        void givenId_whenRemoveEmployeeFromCompanyWithEmployeeNotCreatedWithoutCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            employee.setOwner(false);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.existByUserAndIsOwner(user, true)).thenReturn(Optional.empty());
            when(messageSourceService.get("employee_not_created_without_company")).thenReturn("test");

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                employeeController.removeEmployeeFromCompany(user.getId());
            });

            // Then
            assertEquals("test", exception.getMessage());
        }

        @Test
        void givenId_whenRemoveEmployeeFromCompanyWithEmployeeFromDifferentCompany_thenBadRequestException() {
            // Given
            String userId = "userId123";
            User getUser = Factory.createUser();
            User oneById = Factory.createUser();
            Employee employeeToken = Factory.createEmployee();
            employeeToken.setOwner(true);
            Employee employeeUrl = Factory.createEmployee();

            when(userService.getUser()).thenReturn(getUser);
            when(userService.findOneById(userId)).thenReturn(oneById);
            when(userService.getUser()).thenReturn(getUser);
            when(employeeService.existByUserAndIsOwner(getUser, true))
                    .thenReturn(Optional.of(employeeToken));
            when(employeeService.findByUserAndIsOwner(oneById, false)).thenReturn(employeeUrl);
            when(messageSourceService.get("are_not_allowed_access_employee_another_company")).thenReturn("test");

            // When
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                employeeController.removeEmployeeFromCompany(userId);
            });

            // Then
            assertEquals("test", exception.getMessage());
        }
    }
}