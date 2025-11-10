package com.cosmosboard.fmh.controller.admin;

import com.cosmosboard.fmh.dto.request.city.CreateCityRequest;
import com.cosmosboard.fmh.dto.request.city.UpdateCityRequest;
import com.cosmosboard.fmh.dto.response.city.CitiesPaginationResponse;
import com.cosmosboard.fmh.dto.response.city.CityResponse;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.specification.criteria.CityCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CityService;
import com.cosmosboard.fmh.service.MessageSourceService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Admin - CityController")
public class CityControllerTest {
    @InjectMocks
    private CityController cityController;

    @Mock
    private CityService cityService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("Test class for city list scenarios")
    public class ListTest {
        @Test
        void givenQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenBadRequestExceptionThrown() {
            // Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("TEST Invalid Sort Column");
            // When
            Executable response = () -> cityController.list(null, null, null, "null", null);
            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST Invalid Sort Column", exception.getMessage());
        }
        @Test
        void givenQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            Page<City> page = new PageImpl<>(List.of(city));
            when(cityService.findAll(any(CityCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(page);

            // When
            CitiesPaginationResponse citiesResponse = cityController.list(null, null, null, null, null);

            // Then
            assertNotNull(citiesResponse);
            assertEquals(1, citiesResponse.getPage());
            assertEquals(1, citiesResponse.getPages());
            assertEquals(1, citiesResponse.getTotal());
            assertEquals(1, citiesResponse.getItems().size());
            assertEquals(city.getId(), citiesResponse.getItems().get(0).getId());
            assertEquals(city.getName(), citiesResponse.getItems().get(0).getName());
            assertEquals(city.getCode(), citiesResponse.getItems().get(0).getCode());
        }
    }

    @Nested
    @DisplayName("Test class for city create scenarios")
    public class CreateTest {
        @Test
        void givenCreateCityRequest_whenCreate_thenAssertBody() {
            // Given
            CreateCityRequest request = Factory.createCreateCityRequest();
            City city = Factory.createCity();
            when(cityService.create(request)).thenReturn(city);
            // When
            CityResponse response = cityController.create(request);
            // Then
            assertNotNull(response);
            assertEquals(city.getId(), response.getId());
            assertEquals(city.getName(), response.getName());
        }

        @Test
        void givenInvalidRequest_whenCreate_thenThrowBadRequestException() {
            // Given
            CreateCityRequest invalidRequest = new CreateCityRequest();

            when(cityService.create(invalidRequest)).thenThrow(new BadRequestException("Invalid city data"));

            // When / Then
            assertThrows(BadRequestException.class, () -> cityController.create(invalidRequest));
        }
    }

    @Nested
    @DisplayName("Test class for city show scenarios")
    public class ShowTest {
        @Test
        void givenId_whenShow_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            when(cityService.findOneById(city.getId())).thenReturn(city);

            // When
            CityResponse response = cityController.show(city.getId());

            // Then
            assertNotNull(response);
            assertEquals(city.getId(), response.getId());
            assertEquals(city.getName(), response.getName());
            assertEquals(city.getCode(), response.getCode());
        }
    }

    @Nested
    @DisplayName("Test class for city update scenarios")
    public class UpdateTest {
        @Test
        void givenIdAndUpdateCityRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateCityRequest request = Factory.createUpdateCityRequest();
            City city = Factory.createCity();
            when(cityService.update(city.getId(), request)).thenReturn(city);

            // When
            CityResponse response = cityController.update(city.getId(), request);

            // Then
            assertNotNull(response);
            assertEquals(city.getId(), response.getId());
            assertEquals(city.getName(), response.getName());
            assertEquals(city.getCode(), response.getCode());
        }
    }

    @Nested
    @DisplayName("Test class for city delete scenarios")
    public class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            // When
            ResponseEntity<Void> response = cityController.delete(city.getId());
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }
}

