package com.cosmosboard.fmh.controller.admin;

import com.cosmosboard.fmh.dto.request.company.CompanyOperationRequest;
import com.cosmosboard.fmh.dto.request.company.CreateCompanyRequest;
import com.cosmosboard.fmh.dto.request.company.UpdateCompanyWithStatusAndFmsCompany;
import com.cosmosboard.fmh.dto.response.company.CompanyPaginationResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import com.cosmosboard.fmh.dto.response.user.UserResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.CompanyCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CompanyService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
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
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CompanyControllerTest")
public class CompanyControllerTest {
    @InjectMocks
    private CompanyController companyController;

    @Mock
    private CompanyService companyService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private UserService userService;


    @Nested
    @DisplayName("Test class for Company List scenarios")
    public class ListTest {
        @Test
        void givenEmployeesAndQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenBadRequestException(){
            // Given
            when(messageSourceService.get("invalid_sort_column"))
                    .thenReturn("dummy");
            // When
            Executable closureToTest = () -> companyController.list(null, null,
                    null, null, null, "InvalidSortBy", null);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
        }

        @Test
        void givenEmployeesAndQAndPageAndSizeAndSortByAndSort_whenListWithInvalidEmployee_thenBadRequestException() {
            // Given
            List<String> invalidEmployees = Arrays.asList("invalidId1", "invalidId2");
            when(userService.findOneById(anyString())).thenThrow(NotFoundException.class);
            when(messageSourceService.get("invalid_employee"))
                    .thenReturn("dummy");

            // When
            Executable closureToTest = () -> companyController.list(invalidEmployees, null,
                    null, null, null, null, null);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, closureToTest);
            assertEquals("dummy", exception.getMessage());
        }

        @Test
        void givenEmployeesAndQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody () {
            // Given
            Company company = Factory.createCompany();
            Page<Company> page = new PageImpl<>(List.of(company));
            when(companyService.findAll(any(CompanyCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(page);

            // When
            CompanyPaginationResponse companyResponse = companyController.list(null, null, null, null, null, null, null);

            // Then
            assertNotNull(companyResponse);
            assertEquals(1, companyResponse.getPage());
            assertEquals(1, companyResponse.getPages());
            assertEquals(1, companyResponse.getTotal());
            assertEquals(1, companyResponse.getItems().size());

            CompanyResponse companyItem = companyResponse.getItems().get(0);
            assertEquals(company.getId(), companyItem.getId());
        }

    }

    @Nested
    @DisplayName("Test class for Company show scenarios")
    public class ShowTest {
        @Test
        void givenId_whenShow_thenAssertBody() {
            // Given
            String companyId = "valid_company_id";
            Company mockCompany = Factory.createCompany();

            when(companyService.findOneById(companyId)).thenReturn(mockCompany);

            // When
            CompanyResponse response = companyController.show(companyId);

            // Then
            assertNotNull(response);
            assertEquals(mockCompany.getId(), response.getId());
            assertEquals(mockCompany.getName(), response.getName());
            assertEquals(mockCompany.getDescription(), response.getDescription());
        }
    }

    @Nested
    @DisplayName("Test class for Company CreateCompany scenarios")
    public class CreateCompanyTest {
        @Test
        void givenCreateCompanyRequest_whenCreateCompany_thenAssertBody() {
            // Given
            CreateCompanyRequest request = Factory.createCreateCompanyRequest();
            Company company = Factory.createCompany();

            when(companyService.create(request)).thenReturn(company);

            // When
            CompanyResponse response = companyController.createCompany(request);

            // Then
            assertNotNull(response);
            assertEquals(company.getId(), response.getId());
            assertEquals(company.getName(), response.getName());
        }

        @Test
        void givenInvalidRequest_whenCreateCompany_thenThrowBadRequestException() {
            // Given
            CreateCompanyRequest invalidRequest = new CreateCompanyRequest();

            when(companyService.create(invalidRequest)).thenThrow(new BadRequestException("Invalid company data"));

            // When / Then
            assertThrows(BadRequestException.class, () -> companyController.createCompany(invalidRequest));
        }
    }

    @Nested
    @DisplayName("Test class for Company UpdateStatus scenarios")
    public class UpdateStatusTest {
        @Test
        void givenIdAndStatus_whenUpdateStatus_thenAssertBody() {
            // Given
            String companyId = "valid_company_id";
            String newStatus = "APPROVED";

            Company mockCompany = Factory.createCompany();
            UpdateCompanyWithStatusAndFmsCompany request = Factory.createUpdateCompanyWithStatusAndFmsCompany();
            request.setStatus(newStatus);

            when(companyService.findOneById(companyId)).thenReturn(mockCompany);
            when(companyService.save(any())).thenReturn(mockCompany);

            // When
            CompanyResponse response = companyController.updateStatus(companyId, request);

            // Then
            assertNotNull(response);
            assertEquals(newStatus, response.getStatus());
        }
    }
}