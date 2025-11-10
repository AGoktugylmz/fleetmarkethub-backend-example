package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.company.CreateCompanyRequest;
import com.cosmosboard.fmh.dto.request.company.UpdateCompanyRequest;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.specification.CompanyFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.CompanyRepository;
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
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CompanyService")
public class CompanyServiceTest {
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock private MessageSourceService messageSourceService;

    @Mock private CategoryService categoryService;

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            when(companyRepository.count()).thenReturn(1L);

            // When
            long count = companyService.count();

            // Then
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void given_whenFindAll_thenAssertBody() {
            // Given
            List<Company> expectedCompanies = new ArrayList<>();
            expectedCompanies.add(Factory.createCompany());
            expectedCompanies.add(Factory.createCompany());

            when(companyRepository.findAll()).thenReturn(expectedCompanies);

            // When
            List<Company> actualCompanies = companyService.findAll();

            // Then
            assertEquals(expectedCompanies.size(), actualCompanies.size());
            assertEquals(expectedCompanies.get(1).getName(), actualCompanies.get(1).getName());
        }
    }

    @Nested
    @DisplayName("Test class for findAll with pagination scenarios")
    class FindAllWithPaginationTest {
        @Test
        void givenCompanyCriteriaAndPaginationCriteria_whenFindAll_thenAssertBody() {
            // Given
            Company company = Factory.createCompany();
            Page<Company> page = new PageImpl<>(List.of(company));
            when(companyRepository.findAll(any(CompanyFilterSpecification.class),
                    any(Pageable.class))).thenReturn(page);
            // When
            Page<Company> result = companyService.findAll(null,
                    PaginationCriteria.builder().page(1).size(20).build());
            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getContent().size());
            assertEquals(company.getName(), result.getContent().get(0).getName());
            assertEquals(company.getEmployees(), result.getContent().get(0).getEmployees());
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            Company company = Factory.createCompany();
            when(companyRepository.findById(any(String.class))).thenReturn(Optional.of(company));
            // When
            Company result = companyService.findOneById(company.getId());

            // Then
            assertNotNull(result);
            assertEquals(company, result);
            assertEquals(company.getId(), result.getId());
            assertEquals(company.getStatus(), result.getStatus());
            assertEquals(company.getCreatedAt(), result.getCreatedAt());
            assertEquals(company.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> companyService.findOneById("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("demand_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenCreateCompanyRequest_whenCreate_thenAssertBody() {
            // Given
            CreateCompanyRequest request = Factory.createCreateCompanyRequest();
            Company company = Factory.createCompany();
            request.setName(company.getName());
            request.setDescription(company.getDescription());
            Category category = Factory.createCategory();
            company.setCategory(category);

            when(categoryService.findOneById(any())).thenReturn(category);
            when(companyService.save(any(Company.class))).thenReturn(company);

            // When
            Company createdCompany = companyService.create(request);

            // Then
            assertNotNull(createdCompany);
            assertEquals(request.getName(), createdCompany.getName());
            assertEquals(request.getDescription(), createdCompany.getDescription());
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenIdAndUpdateCompanyRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateCompanyRequest request = Factory.createUpdateCompanyRequest();
            Company company = Factory.createCompany();
            request.setName(company.getName());
            request.setDescription(company.getDescription());

            when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));
            when(categoryService.findOneById(any())).thenReturn(Factory.createCategory());
            when(companyService.save(any(Company.class))).thenReturn(company);

            // When
            Company updatedCompany = companyService.update(company.getId(), request);

            // Then
            assertEquals(request.getName(), updatedCompany.getName());
            assertEquals(request.getDescription(), updatedCompany.getDescription());
        }
    }

    @Nested
    @DisplayName("Test class for save scenarios")
    class SaveTest {
        @Test
        void givenCompany_whenSave_ThenAssertBody() {
            Company savedCompany = Factory.createCompany();

            when(companyRepository.save(savedCompany)).thenReturn(savedCompany);

            // When
            Company result = companyService.save(savedCompany);

            // Then
            assertEquals(savedCompany, result);
            verify(companyRepository, times(1)).save(savedCompany);
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenId_whenDelete_ThenAssertBody() {
            // Given
            Company company = Factory.createCompany();

            when(companyRepository.findById(company.getId())).thenReturn(java.util.Optional.of(company));

            // When
            companyService.delete(company.getId());

            // Then
            verify(companyRepository, times(1)).delete(company);
        }
    }
}
