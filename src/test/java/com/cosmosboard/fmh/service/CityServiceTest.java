package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.city.CreateCityRequest;
import com.cosmosboard.fmh.dto.request.city.UpdateCityRequest;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.specification.CityFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.CityRepository;
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
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CityService")
class CityServiceTest {
    @InjectMocks
    CityService cityService;

    @Mock
    CityRepository cityRepository;

    @Mock
    MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            when(cityService.count()).thenReturn(1L);
            // When
            Long count = cityService.count();
            // Then
            assertNotNull(count);
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void given_whenFindAll_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            List<City> cities = List.of(city);
            when(cityService.findAll()).thenReturn(cities);
            // When
            List<City> result = cityService.findAll();
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(city, result.get(0));
            assertEquals(city.getId(), result.get(0).getId());
            assertEquals(city.getName(), result.get(0).getName());
            assertEquals(city.getCode(), result.get(0).getCode());
            assertEquals(city.getCreatedAt(), result.get(0).getCreatedAt());
            assertEquals(city.getUpdatedAt(), result.get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findAll with pagination scenarios")
    class FindAllWithPaginationTest {
        @Test
        void givenCityCriteriaAndPaginationCriteria_whenFindAllWithPagination_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            Page<City> cities = new PageImpl<>(List.of(city));
            when(cityRepository.findAll(any(CityFilterSpecification.class),
                    any(Pageable.class))).thenReturn(cities);
            // When
            Page<City> result = cityService.findAll(null,
                    PaginationCriteria.builder().page(1).size(20).build());
            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getContent().size());
            assertEquals(city, result.getContent().get(0));
            assertEquals(city.getId(), result.getContent().get(0).getId());
            assertEquals(city.getName(), result.getContent().get(0).getName());
            assertEquals(city.getCode(), result.getContent().get(0).getCode());
            assertEquals(city.getCreatedAt(), result.getContent().get(0).getCreatedAt());
            assertEquals(city.getUpdatedAt(), result.getContent().get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            when(cityRepository.findById(any(String.class))).thenReturn(Optional.of(city));
            // When
            City result = cityService.findOneById(city.getId());
            // Then
            assertNotNull(result);
            assertEquals(city, result);
            assertEquals(city.getId(), result.getId());
            assertEquals(city.getName(), result.getName());
            assertEquals(city.getCode(), result.getCode());
            assertEquals(city.getCreatedAt(), result.getCreatedAt());
            assertEquals(city.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> cityService.findOneById("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("city_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenCreateCityRequest_whenCreateWithCityAlreadyExists_thenThrowBadRequestException() {
            // Given
            CreateCityRequest request = Factory.createCreateCityRequest();
            when(cityRepository.existsByNameOrCode(request.getName(), request.getCode())).thenReturn(true);
            // When
            Executable closureToTest = () -> cityService.create(request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("city_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }
        @Test
        void givenCreateCityRequest_whenCreate_thenAssertBody() {
            // Given
            CreateCityRequest request = Factory.createCreateCityRequest();
            City city = Factory.createCity();
            when(cityRepository.existsByNameOrCode(request.getName(), request.getCode())).thenReturn(false);
            when(cityRepository.save(any(City.class))).thenReturn(city);
            // When
            City result = cityService.create(request);
            // Then
            assertNotNull(result);
            assertEquals(city, result);
            assertEquals(city.getId(), result.getId());
            assertEquals(city.getName(), result.getName());
            assertEquals(city.getCode(), result.getCode());
            assertEquals(city.getCreatedAt(), result.getCreatedAt());
            assertEquals(city.getUpdatedAt(), result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenIdAndUpdateCityRequest_whenUpdate_thenThrowNotFoundException() {
            // Given
            UpdateCityRequest request = Factory.createUpdateCityRequest();
            // When
            Executable closureToTest = () -> cityService.update("xxx", request);
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("city_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
        @Test
        void givenIdAndUpdateCityRequest_whenUpdateWithCityAlreadyExists_thenThrowBadRequestException() {
            // Given
            UpdateCityRequest request = Factory.createUpdateCityRequest();
            City city = Factory.createCity();
            when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
            when(cityRepository.existByNameOrCodeAndIdNotIdNot(request.getName(), request.getCode(), city.getId()))
                    .thenReturn(true);
            // When
            Executable closureToTest = () -> cityService.update(city.getId(), request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("city_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }
        @Test
        void givenIdAndUpdateCityRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateCityRequest request = Factory.createUpdateCityRequest();
            request.setName("New York City");
            request.setCode("NYC");
            City city = Factory.createCity();
            when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
            when(cityRepository.existByNameOrCodeAndIdNotIdNot(request.getName(), request.getCode(), city.getId()))
                    .thenReturn(false);
            when(cityRepository.save(any(City.class))).thenReturn(city);

            // When
            City result = cityService.update(city.getId(), request);

            // Then
            assertNotNull(result);
            assertEquals(city, result);
            assertEquals(city.getId(), result.getId());
            assertEquals(request.getName(), result.getName());
            assertEquals(request.getCode(), result.getCode());
            assertEquals(city.getCreatedAt(), result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
            doNothing().when(cityRepository).delete(city);
            // When
            Executable closureToTest = () -> cityService.delete(city.getId());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenId_whenDelete_thenThrowNotFoundException() {
            // Given
            City city = Factory.createCity();
            city.setId(null);
            // When
            Executable closureToTest = () -> cityService.delete(city.getId());
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("city_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }
}
