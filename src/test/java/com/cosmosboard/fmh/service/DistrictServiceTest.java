package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.district.CreateDistrictRequest;
import com.cosmosboard.fmh.dto.request.district.UpdateDistrictRequest;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.entity.specification.DistrictFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.DistrictRepository;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for DistrictService")
class DistrictServiceTest {
    @InjectMocks
    private DistrictService districtService;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private CityService cityService;

    @Mock
    private MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void givenCityId_whenFindAll_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            List<District> districts = List.of(district);
            when(districtService.findAll(district.getCity().getId())).thenReturn(districts);
            // When
            List<District> result = districtService.findAll(district.getCity().getId());
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(district, result.get(0));
            assertEquals(district.getId(), result.get(0).getId());
            assertEquals(district.getName(), result.get(0).getName());
            assertEquals(district.getCreatedAt(), result.get(0).getCreatedAt());
            assertEquals(district.getUpdatedAt(), result.get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findAll with pagination scenarios")
    class FindAllWithPaginationTest {
        @Test
        void givenDistrictCriteriaAndPaginationCriteria_whenFindAllWithPagination_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            Page<District> districts = new PageImpl<>(List.of(district));
            when(districtRepository.findAll(any(DistrictFilterSpecification.class),
                    any(Pageable.class))).thenReturn(districts);
            // When
            Page<District> result = districtService.findAll(null,
                    PaginationCriteria.builder().page(1).size(20).build());
            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getContent().size());
            assertEquals(district, result.getContent().get(0));
            assertEquals(district.getId(), result.getContent().get(0).getId());
            assertEquals(district.getName(), result.getContent().get(0).getName());
            assertEquals(district.getCreatedAt(), result.getContent().get(0).getCreatedAt());
            assertEquals(district.getUpdatedAt(), result.getContent().get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            when(districtRepository.findById(any(String.class))).thenReturn(Optional.of(district));
            // When
            District result = districtService.findOneById(district.getId());
            // Then
            assertNotNull(result);
            assertEquals(district, result);
            assertEquals(district.getId(), result.getId());
            assertEquals(district.getName(), result.getName());
            assertEquals(district.getCreatedAt(), result.getCreatedAt());
            assertEquals(district.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> districtService.findOneById("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("district_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for findOneByIdAndCityId scenarios")
    class FindOneByIdAndCityIdTest {
        @Test
        void givenIdAndCityId_whenFindOneByIdAndCityId_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            when(districtRepository.findByIdAndCityId(any(String.class), any(String.class)))
                    .thenReturn(Optional.of(district));
            // When
            District result = districtService.findOneByIdAndCityId(district.getId(), district.getCity().getId());
            // Then
            assertNotNull(result);
            assertEquals(district, result);
            assertEquals(district.getId(), result.getId());
            assertEquals(district.getName(), result.getName());
            assertEquals(district.getCreatedAt(), result.getCreatedAt());
            assertEquals(district.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenIdAndCityId_whenFindOneByIdAndCityId_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> districtService.findOneByIdAndCityId("xxx", "xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("district_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenCreateDistrictRequest_whenCreateWithDistrictAlreadyExists_thenThrowBadRequestException() {
            // Given
            CreateDistrictRequest request = Factory.createCreateDistrictRequest();
            when(districtRepository.existsByCityIdAndName(request.getCityId(), request.getName())).thenReturn(true);
            // When
            Executable closureToTest = () -> districtService.create(request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("district_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }

        @Test
        void givenCreateDistrictRequest_whenCreate_thenAssertBody() {
            // Given
            CreateDistrictRequest request = Factory.createCreateDistrictRequest();
            District district = Factory.createDistrict();
            when(districtRepository.existsByCityIdAndName(request.getCityId(), request.getName())).thenReturn(false);
            when(districtRepository.save(any(District.class))).thenReturn(district);
            // When
            District result = districtService.create(request);
            // Then
            assertNotNull(result);
            assertEquals(district, result);
            assertEquals(district.getId(), result.getId());
            assertEquals(district.getName(), result.getName());
            assertEquals(district.getCity().getId(), result.getCity().getId());
            assertEquals(district.getCreatedAt(), result.getCreatedAt());
            assertEquals(district.getUpdatedAt(), result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenIdAndUpdateDistrictRequest_whenUpdate_thenThrowNotFoundException() {
            // Given
            UpdateDistrictRequest request = Factory.createUpdateDistrictRequest();
            // When
            Executable closureToTest = () -> districtService.update("xxx", request);
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("district_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }

        @Test
        void givenIdAndUpdateDistrictRequest_whenUpdate_thenThrowBadRequestException_FromDistrictAlreadyExists() {
            // Given
            UpdateDistrictRequest request = new UpdateDistrictRequest();
            District district = Factory.createDistrict();
            when(districtRepository.findById(district.getId())).thenReturn(Optional.of(district));
            when(cityService.findOneById(district.getCity().getId())).thenReturn(district.getCity());
            when(districtRepository.existsByCityIdAndNameAndIdNot(request.getCityId(), request.getName(),
                    district.getId())).thenReturn(true);
            // When
            Executable closureToTest = () -> districtService.update(district.getId(), request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("district_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }
        @Test
        void givenIdAndUpdateDistrictRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateDistrictRequest request = Factory.createUpdateDistrictRequest();
            request.setName("New District Name");
            request.setCityId("1234");

            City city = Factory.createCity();
            District district = Factory.createDistrict();

            when(districtRepository.findById(district.getId())).thenReturn(Optional.of(district));
            when(cityService.findOneById(request.getCityId())).thenReturn(city);
            when(districtRepository.existsByCityIdAndNameAndIdNot(request.getCityId(), request.getName(),
                    district.getId())).thenReturn(false);
            when(districtRepository.save(any(District.class))).thenReturn(district);

            // When
            District result = districtService.update(district.getId(), request);

            // Then
            assertNotNull(result);
            assertEquals(district, result);
            assertEquals(district.getId(), result.getId());
            assertEquals(request.getName(), result.getName());
            assertEquals(city.getId(), result.getCity().getId());
            assertEquals(district.getCreatedAt(), result.getCreatedAt());
            assertEquals(district.getUpdatedAt(), result.getUpdatedAt());
            verify(districtRepository, times(1)).save(district);
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            when(districtRepository.findById(district.getId())).thenReturn(Optional.of(district));
            doNothing().when(districtRepository).delete(district);
            // When
            Executable closureToTest = () -> districtService.delete(district.getId());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenId_whenDelete_thenThrowNotFoundException() {
            // Given
            District district = Factory.createDistrict();
            district.setId(null);
            // When
            Executable closureToTest = () -> districtService.delete(district.getId());
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("district_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }
}

