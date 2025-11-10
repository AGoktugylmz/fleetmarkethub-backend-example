package com.cosmosboard.fmh.controller.admin;

import com.cosmosboard.fmh.dto.request.car.brand.CreateCarBrandRequest;
import com.cosmosboard.fmh.dto.request.car.brand.UpdateCarBrandRequest;
import com.cosmosboard.fmh.dto.response.car.brand.CarBrandPaginationResponse;
import com.cosmosboard.fmh.dto.response.car.brand.CarBrandResponse;
import com.cosmosboard.fmh.entity.CarBrand;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CarBrandService;
import com.cosmosboard.fmh.service.MessageSourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Admin - CarBrandController")
public class CarBrandControllerTest {
    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private CarBrandService carBrandService;

    @InjectMocks
    private CarBrandController carBrandController;

    @Nested
    @DisplayName("Test class for CarBrand list scenarios")
    public class ListTest {
        @Test
        void givenQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenBadRequestExceptionThrown() {
            // Given
            String invalidSortBy = "invalidColumn";

            String errorMessage = "Invalid sort column";
            when(messageSourceService.get(anyString())).thenReturn(errorMessage);

            // When
            BadRequestException exception = assertThrows(BadRequestException.class, () -> {
                carBrandController.list("Lorem", 1, 20, invalidSortBy, "desc");
            });

            // Then
            assertEquals(errorMessage, exception.getMessage());
            verify(messageSourceService, times(1)).get("invalid_sort_column");
            verify(carBrandService, times(0)).findAll(any(), any());
        }

        @Test
        void givenQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            String name = "Lorem";
            Integer page = 1;
            Integer size = 20;

            Page mockedPage = mock(Page.class);
            when(carBrandService.findAll(any(), any())).thenReturn(mockedPage);

            List<CarBrand> carBrands = List.of(new CarBrand());
            when(mockedPage.stream()).thenReturn(carBrands.stream());

            // When
            CarBrandPaginationResponse response = carBrandController.list(name, page, size, null, null);

            // Then
            assertNotNull(response);
            assertNotNull(response.getItems());
            verify(carBrandService, times(1)).findAll(any(), any());
        }
    }

    @Nested
    @DisplayName("Test class for CarBrand create scenarios")
    public class CreateTest {
        @Test
        void givenCreateCarBrandRequest_whenCreate_thenAssertBody() {
            // Given
            CreateCarBrandRequest createCarBrandRequest = Factory.createCreateCarBrandRequest();
            createCarBrandRequest.setName("Brand1");

            CarBrand carBrand = new CarBrand();
            carBrand.setId("1L");
            carBrand.setName(createCarBrandRequest.getName());

            CarBrandResponse expectedResponse = CarBrandResponse.convert(carBrand);

            when(carBrandService.create(any(CreateCarBrandRequest.class))).thenReturn(carBrand);

            // When
            CarBrandResponse response = carBrandController.create(createCarBrandRequest);

            // Then
            assertNotNull(response);
            assertEquals(expectedResponse.getId(), response.getId());
            assertEquals(expectedResponse.getName(), response.getName());
        }
    }

    @Nested
    @DisplayName("Test class for CarBrand Update scenarios")
    public class UpdateTest {
        @Test
        void givenUpdateCarBrandRequest_whenUpdate_thenAssertBody() {
            // Given
            String id = "1";
            UpdateCarBrandRequest updateCarBrandRequest = Factory.createUpdateCarBrandRequest();
            updateCarBrandRequest.setName("Updated Brand");

            CarBrand carBrand = Factory.createCarBrand();
            carBrand.setName(updateCarBrandRequest.getName());

            CarBrandResponse expectedResponse = CarBrandResponse.convert(carBrand);

            when(carBrandService.update(eq(id), any(UpdateCarBrandRequest.class))).thenReturn(carBrand);

            // When
            CarBrandResponse response = carBrandController.update(id, updateCarBrandRequest);

            // Then
            assertNotNull(response);
            assertEquals(expectedResponse.getId(), response.getId());
            assertEquals(expectedResponse.getName(), response.getName());
        }
    }

    @Nested
    @DisplayName("Test class for CarBrand Delete scenarios")
    public class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            String id = "1";

            // When
            doNothing().when(carBrandService).delete(eq(id));
            ResponseEntity<Void> response = carBrandController.delete(id);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(carBrandService, times(1)).delete(eq(id));
        }
    }
}
