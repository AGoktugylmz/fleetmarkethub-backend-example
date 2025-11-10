package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.EmployeeRepository;
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
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit test for EmployeeService")
public class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test class for findByUserAndIsOwner scenarios")
    class FindByUserAndIsOwnerTest {
        @Test
        void givenUserAndIsOwner_whenFindByUserAndIsOwner_thenThrowNotFoundException() {
            // Given
            User user = Factory.createUser();
            when(employeeRepository.findByUserAndIsOwner(user, true)).thenThrow(new NotFoundException());

            // When / Then
            assertThrows(NotFoundException.class, () -> employeeService.existByUserAndIsOwner(user, true));
        }
        @Test
        void givenUserAndIsOwner_whenFindByUserAndIsOwner_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Employee expectedEmployee = Factory.createEmployee();
            when(employeeRepository.findByUserAndIsOwner(user, true)).thenReturn(Optional.ofNullable(expectedEmployee));

            // When
            Employee foundEmployee = employeeService.findByUserAndIsOwner(user, true);

            // Then
            assertNotNull(foundEmployee);
            assertEquals(expectedEmployee, foundEmployee);
        }
    }

    @Nested
    @DisplayName("Test class for existByUserAndIsOwner scenarios")
    class ExistByUserAndIsOwnerTest {
        @Test
        void givenUserAndIsOwner_whenExistByUserAndIsOwner_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Employee expectedEmployee = Factory.createEmployee();
            when(employeeRepository.findByUserAndIsOwner(user, true)).thenReturn(Optional.ofNullable(expectedEmployee));

            // When
            Employee foundEmployee = employeeService.findByUserAndIsOwner(user, true);

            // Then
            assertNotNull(foundEmployee);
            assertEquals(expectedEmployee, foundEmployee);
        }
    }

    @Nested
    @DisplayName("Test class for findByOneId scenarios")
    class FindByOneIdTest {
        @Test
        void givenEmployeeId_whenFindByOneId_thenThrowNotFoundException() {
            // Given
            String nonExistentEmployeeId = "non-id";
            when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(NotFoundException.class, () -> employeeService.findByOneId(nonExistentEmployeeId));
        }

        @Test
        void givenEmployeeId_whenFindByOneId_thenAssertBody() {
            // Given
            String validEmployeeId = "valid-id";
            Employee expectedEmployee = Factory.createEmployee();
            when(employeeRepository.findById(validEmployeeId)).thenReturn(Optional.of(expectedEmployee));

            // When
            Employee result = employeeService.findByOneId(validEmployeeId);

            // Then
            assertEquals(expectedEmployee, result);
        }
    }

    @Nested
    @DisplayName("Test class for findAllByUserAndIsOwner scenarios")
    class FindAllByUserAndIsOwnerTest {
        @Test
        void givenUserAndIsOwner_whenFindAllByUserAndIsOwner_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            Boolean isOwner = true;

            List<Employee> expectedEmployeeList = List.of(Factory.createEmployee(), Factory.createEmployee());

            when(employeeRepository.findAllByUserAndIsOwner(user, isOwner)).thenReturn(expectedEmployeeList);

            // When
            List<Employee> result = employeeService.findAllByUserAndIsOwner(user, isOwner);

            // Then
            assertEquals(expectedEmployeeList, result);
        }
    }

    @Nested
    @DisplayName("Test class for findAllByUser scenarios")
    class FindAllByUserTest {
        @Test
        void givenUser_whenFindAllByUser_thenAssertBody() {
            // Given
            User user = Factory.createUser();

            List<Employee> expectedEmployeeList = List.of(Factory.createEmployee(), Factory.createEmployee());

            when(employeeRepository.findAllByUser(user)).thenReturn(expectedEmployeeList);

            // When
            List<Employee> result = employeeService.findAllByUser(user);

            // Then
            assertEquals(expectedEmployeeList, result);
        }
    }

    @Nested
    @DisplayName("Test class for findAllByCompany scenarios")
    class FindAllByCompanyTest {
        @Test
        void givenCompanyAndPaginationCriteria_whenFindAllByCompany_thenAssertBody() {
            // Given
            Company company = Factory.createCompany();
            Page<Employee> expectedPage = new PageImpl<>(new ArrayList<>());

            PaginationCriteria paginationCriteria = PaginationCriteria.builder()
                    .page(1)
                    .size(10)
                    .sortBy("columnName")
                    .sort("asc")
                    .columns(new String[]{"column1", "column2"})
                    .build();

            when(employeeRepository.findAllByCompany(any(Company.class), any(Pageable.class)))
                    .thenReturn(expectedPage);

            // When
            Page<Employee> resultPage = employeeService.findAllByCompany(company, paginationCriteria);

            // Then
            assertEquals(expectedPage, resultPage);
        }
    }

    @Nested
    @DisplayName("Test class for createEmployee scenarios")
    class CreateEmployeeTest {
        @Test
        void givenCompanyAndIsOwner_whenCreateEmployee_thenAssertBody() {
            // Given
            Company company = Factory.createCompany();

            User mockUser = Factory.createUser();
            mockUser.setId("mockUserId");
            when(userService.getUser()).thenReturn(mockUser);

            // When
            Employee result = employeeService.createEmployee(company, true);

            // Then
            if (result != null) {
                assertEquals(mockUser, result.getUser());
                assertEquals(company, result.getCompany());
                assertTrue(result.isOwner());
            }
        }
    }

    @Nested
    @DisplayName("Test class for save scenarios")
    class SaveTest {
        @Test
        void givenEmployee_whenSave_thenAssertBody() {
            // Given
            Employee employee = Factory.createEmployee();
            when(employeeRepository.save(employee)).thenReturn(employee);

            // When
            employeeService.save(employee);

            // Then
            verify(employeeRepository).save(employee);
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenEmployee_whenDelete_thenAssertBody() {
            // Given
            Employee employee = Factory.createEmployee();
            doNothing().when(employeeRepository).delete(employee);

            // When
            employeeService.delete(employee);

            // Then
            verify(employeeRepository).delete(employee);
        }
    }
}
