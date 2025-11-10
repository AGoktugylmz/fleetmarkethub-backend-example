package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.company.CreateCompanyRequest;
import com.cosmosboard.fmh.dto.request.company.UpdateCompanyRequest;
import com.cosmosboard.fmh.dto.request.location.CreateLocationRequest;
import com.cosmosboard.fmh.dto.request.location.UpdateLocationRequest;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import com.cosmosboard.fmh.dto.response.location.LocationResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Location;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CompanyService;
import com.cosmosboard.fmh.service.EmployeeService;
import com.cosmosboard.fmh.service.LocationService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CompanyController")
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompanyControllerTest {
    @InjectMocks
    private CompanyController companyController;

    @Mock
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private LocationService locationService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private EmployeeService employeeService;

    @Nested
    @DisplayName("Test class for Company getCompany scenarios")
    class GetCompanyTest {
        @Test
        void givenId_whenGetCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            user.setEmployees(new ArrayList<>());
            when(userService.getUser()).thenReturn(user);
            when(messageSourceService.get("invalid_company")).thenReturn("Test Error Message");

            // When/Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> companyController.getCompany());
            assertEquals("Test Error Message", exception.getMessage());
            verify(userService, times(1)).getUser();
            verify(messageSourceService, times(1)).get("invalid_company");
        }

        @Test
        void givenId_whenGetCompany_thenAssertBody () {
            // Given
            String companyId = "testCompanyId";
            Company company = Factory.createCompany();
            company.setId(companyId);

            User user = Factory.createUser();
            user.setId("userId");

            Employee employee = Factory.createEmployee();
            employee.setCompany(company);
            employee.setUser(user);

            List<Employee> employeeList = List.of(employee);
            user.setEmployees(employeeList);

            when(userService.getUser()).thenReturn(user);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(employeeService.findAllByUser(user)).thenReturn(employeeList);

            // When
            CompanyResponse response = companyController.getCompany();

            // Then
            assertNotNull(response);
        }
    }

    @Nested
    @DisplayName("Test class for Company updateCompany scenarios")
    class UpdateCompanyTest {
        @Test
        void givenUpdateCompanyRequest_whenUpdateCompany_thenBadRequestException() {
            // Given
            User user = Factory.createUser();
            user.setEmployees(List.of());

            UpdateCompanyRequest request = Factory.createUpdateCompanyRequest();

            when(userService.getUser()).thenReturn(user);
            when(messageSourceService.get("invalid_company")).thenReturn("Invalid company");

            // When
            Executable response = () -> companyController.updateCompany(request);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("Invalid company", exception.getMessage());
        }

        @Test
        @MockitoSettings(strictness = Strictness.LENIENT)
        void givenUpdateCompanyRequest_whenUpdateCompany_thenAssertBody() {
            // Given
            String companyId = "testCompanyId";
            Company existingCompany = Factory.createCompany();
            existingCompany.setId(companyId);
            existingCompany.setName("Test Company");

            User user = Factory.createUser();
            user.setId("userId");

            Employee employee = Factory.createEmployee();
            employee.setCompany(existingCompany);
            employee.setUser(user);
            employee.setOwner(true);

            List<Employee> employeeList = List.of(employee);
            user.setEmployees(employeeList);

            UpdateCompanyRequest request = Factory.createUpdateCompanyRequest();

            when(userService.getUser()).thenReturn(user);
            when(companyService.update(companyId, request)).thenReturn(existingCompany);
            when(employeeService.findAllByUser(user)).thenReturn(employeeList);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);

            // When
            CompanyResponse response = companyController.updateCompany(request);

            // Then
            assertNotNull(response);
            assertEquals("Test Company", response.getName());
        }
    }

    @Nested
    @DisplayName("Test class for Company getLocation scenarios")
    class GetLocationTest {

        @MockitoSettings(strictness = Strictness.LENIENT)
        @Test
        void givenCompanyId_whenGetLocation_thenAssertBody() {
            // Given
            String companyId = "valid_company_id";
            User user = Factory.createUser();
            user.setId("userId");

            Company company = Factory.createCompany();
            company.setId(companyId);
            company.setName("Test Company");

            Employee employee = Factory.createEmployee();
            employee.setUser(user);
            employee.setCompany(company);

            List<Employee> employeeList = List.of(employee);
            user.setEmployees(employeeList);

            List<Location> locations = List.of(Factory.createLocation(), Factory.createLocation());
            company.setLocations(locations);

            when(userService.getUser()).thenReturn(user);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(employeeService.findAllByUser(user)).thenReturn(employeeList);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);

            // When
            List<LocationResponse> locationResponses = companyController.getLocations();

            // Then
            assertNotNull(locationResponses);
            assertEquals(locations.size(), locationResponses.size());
        }
    }

    @Nested
    @DisplayName("Test class for Company createLocation scenarios")
    class CreateLocationTest {
        @Test
        void givenCompanyIdAndCreateLocationRequest_whenCreateLocation_thenAssertBody() {
            // Given
            String companyId = "testCompanyId";
            Company company = Factory.createCompany();
            company.setId(companyId);

            User user = Factory.createUser();
            user.setId("userId");

            Employee employee = Factory.createEmployee();
            employee.setCompany(company);
            employee.setUser(user);
            employee.setOwner(true);

            List<Employee> employeeList = List.of(employee);
            user.setEmployees(employeeList);

            CreateLocationRequest request = Factory.createCreateLocationRequest();
            Location location = Factory.createLocation();
            location.setCompany(company);

            when(userService.getUser()).thenReturn(user);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(locationService.createLocationFromDto(request, company)).thenReturn(location);
            when(locationService.save(location)).thenReturn(location);

            // When
            ResponseEntity<LocationResponse> response = companyController.createLocation(request);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(location.getId(), response.getBody().getId());
        }
    }

    @Nested
    @DisplayName("Test class for Company updateLocation scenarios")
    class UpdateLocationTest {
        @Test
        void givenCompanyIdAndLocationIdAndCreateLocationRequest_whenUpdateLocationWithCompanyNotBelongUser_thenBadRequestException() {
            // Given
            String companyId = "testCompanyId";
            String locationId = "valid_location_id";
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            Company company = Factory.createCompany();
            employee.setCompany(company);

            UpdateLocationRequest request = Factory.createUpdateLocationRequest();

            when(userService.getUser()).thenReturn(user);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(messageSourceService.get("company_does_not_belong_user", new String[]{companyId, user.getId()}))
                    .thenReturn("TEST");

            // When
            Executable response = () -> companyController.updateLocation(companyId, request);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenCompanyIdAndLocationIdAndCreateLocationRequest_whenUpdateLocationWithLocationNotBelongUser_thenBadRequestException() {
            // Given
            String companyId = "valid_company_id";
            String locationId = "valid_location_id";
            UpdateLocationRequest request = Factory.createUpdateLocationRequest();

            Company company = Factory.createCompany();
            Location location = Factory.createLocation();
            Employee employee = Factory.createEmployee();
            User user = Factory.createUser();
            company.setEmployees(List.of(employee));
            employee.setCompany(company);
            employee.setUser(user);

            company.setId(companyId);
            location.setCompany(company);
            employee.setOwner(true);
            employee.setCompany(company);
            user.setEmployees(List.of(employee));

            when(userService.getUser()).thenReturn(user);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(locationService.findOneById(locationId)).thenReturn(location);
            when(messageSourceService.get("location_does_not_belong_company",
                    new String[] {locationId, companyId})).thenReturn("TEST");

            // When & Then
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                companyController.updateLocation(locationId, request);
            });

            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenCompanyIdAndLocationIdAndCreateLocationRequest_whenUpdateLocation_thenAssertBody() {
            // Given
            String companyId = "valid_company_id";
            String locationId = "valid_location_id";
            UpdateLocationRequest request = Factory.createUpdateLocationRequest();

            Company company = Factory.createCompany();
            Location location = Factory.createLocation();
            Employee employee = Factory.createEmployee();
            User user = Factory.createUser();
            company.setEmployees(List.of(employee));
            employee.setCompany(company);
            employee.setUser(user);

            company.setId(companyId);
            location.setCompany(company);
            employee.setOwner(true);
            employee.setCompany(company);
            user.setEmployees(List.of(employee));

            location.setCompany(company);
            company.setLocations(List.of(location));

            when(userService.getUser()).thenReturn(user);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(locationService.findOneById(locationId)).thenReturn(location);
            when(locationService.updateLocationFromDto(request, location)).thenReturn(location);
            when(locationService.save(location)).thenReturn(location);

            // When
            LocationResponse response = companyController.updateLocation(locationId, request);

            // Then
            assertNotNull(response);
        }
    }

    @Nested
    @DisplayName("Test class for Company deleteLocation scenarios")
    class DeleteLocationTest {
        @Test
        void givenCompanyIdAndLocationIdAndCreateLocationRequest_whenDeleteLocationWithCompanyNotBelongUser_thenBadRequestException() {
            // Given
            String companyId = "testCompanyId";
            String locationId = "valid_location_id";
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();
            Company company = Factory.createCompany();
            employee.setCompany(company);

            when(userService.getUser()).thenReturn(user);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(messageSourceService.get("company_does_not_belong_user", new String[]{companyId, user.getId()}))
                    .thenReturn("TEST");

            // When
            Executable response = () -> companyController.deleteLocation(locationId);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenCompanyIdAndLocationIdAndCreateLocationRequest_whenDeleteLocationWithLocationNotBelongUser_thenBadRequestException() {
            // Given
            String companyId = "valid_company_id";
            String locationId = "valid_location_id";

            Company company = Factory.createCompany();
            Location location = Factory.createLocation();
            Employee employee = Factory.createEmployee();
            User user = Factory.createUser();

            company.setId(companyId);
            employee.setOwner(true);
            employee.setCompany(company);
            user.setEmployees(List.of(employee));

            when(userService.getUser()).thenReturn(user);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(locationService.findOneById(locationId)).thenReturn(location);

            when(messageSourceService.get(eq("location_does_not_belong_company"),
                    eq(new String[]{locationId, companyId}))).thenReturn("TEST");

            // When & Then

            Executable response = () -> companyController.deleteLocation(locationId);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST", exception.getMessage());

        }
        @Test
        void givenCompanyIdAndLocationIdAndCreateLocationRequest_whenDeleteLocation_thenAssertBody() {
            // Given
            String companyId = "valid_company_id";
            String locationId = "valid_location_id";

            Company company = Factory.createCompany();
            Location location = Factory.createLocation();
            User user = Factory.createUser();
            Employee employee = Factory.createEmployee();

            employee.setOwner(true);
            location.setId(locationId);
            company.setId(companyId);
            company.setLocations(List.of(location));

            employee.setUser(user);
            employee.setCompany(company);
            user.setEmployees(List.of(employee));

            when(userService.getUser()).thenReturn(user);
            when(employeeService.findByUserAndIsOwner(user, true)).thenReturn(employee);
            when(companyService.findOneById(companyId)).thenReturn(company);
            when(locationService.findOneById(locationId)).thenReturn(location);

            // When
            ResponseEntity<SuccessResponse> response = companyController.deleteLocation(locationId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(
                    String.format("Location with ID %s has been successfully deleted.", locationId),
                    response.getBody().getMessage()
            );
        }
    }
}

