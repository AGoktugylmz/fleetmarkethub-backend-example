package com.cosmosboard.fmh.controller.admin;

import com.cosmosboard.fmh.dto.request.district.CreateDistrictRequest;
import com.cosmosboard.fmh.dto.request.district.UpdateDistrictRequest;
import com.cosmosboard.fmh.dto.response.district.DistrictResponse;
import com.cosmosboard.fmh.dto.response.district.DistrictsPaginationResponse;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.entity.specification.criteria.DistrictCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.DistrictService;
import com.cosmosboard.fmh.service.MessageSourceService;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Admin - DistrictController")
public class DistrictControllerTest {
    @InjectMocks
    private DistrictController districtController;

    @Mock
    private DistrictService districtService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("Test class for district list scenarios")
    public class ListTest {
        @Test
        void givenCityIdAndQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenBadRequestException () {
            // Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("TEST Invalid Sort Column");
            // When
            Executable response = () -> districtController.list(null, null, null, null, "invalidSortBy", null);
            // Then
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class, response);
            Assertions.assertEquals("TEST Invalid Sort Column", exception.getMessage());
        }
        @Test
        void givenCityIdAndQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            Page<District> page = new PageImpl<>(List.of(district));
            when(districtService.findAll(any(DistrictCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(page);

            // When
            DistrictsPaginationResponse districtsResponse = districtController.list(null, null, null, null, null, null);

            // Then
            Assertions.assertNotNull(districtsResponse);
            Assertions.assertEquals(1, districtsResponse.getPage());
            Assertions.assertEquals(1, districtsResponse.getPages());
            Assertions.assertEquals(1, districtsResponse.getTotal());
            Assertions.assertEquals(1, districtsResponse.getItems().size());

            DistrictResponse districtResponse = districtsResponse.getItems().get(0);
            Assertions.assertEquals(district.getId(), districtResponse.getId());
            Assertions.assertEquals(district.getName(), districtResponse.getName());
            Assertions.assertEquals(district.getCity().getId(), districtResponse.getCity().getId());
        }
    }

    @Nested
    @DisplayName("Test class for district create scenarios")
    public class CreateTest {
        @Test
        void givenCreateDistrictRequest_whenCreate_thenAssertBody() {
            // Given
            CreateDistrictRequest request = Factory.createCreateDistrictRequest();
            District district = Factory.createDistrict();
            when(districtService.create(request)).thenReturn(district);
            // When
            ResponseEntity<DistrictResponse> response = districtController.create(request);
            // Then
            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getHeaders());
            Assertions.assertNull(response.getHeaders().getLocation());
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Test class for district show scenarios")
    public class ShowTest {
        @Test
        void givenId_whenShow_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            when(districtService.findOneById(district.getId())).thenReturn(district);

            // When
            DistrictResponse response = districtController.show(district.getId());

            // Then
            Assertions.assertNotNull(response);
            Assertions.assertEquals(district.getId(), response.getId());
            Assertions.assertEquals(district.getName(), response.getName());
            Assertions.assertEquals(district.getCity().getId(), response.getCity().getId());
        }
    }

    @Nested
    @DisplayName("Test class for district update scenarios")
    public class UpdateTest {
        @Test
        void givenIdAndUpdateDistrictRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateDistrictRequest request = Factory.createUpdateDistrictRequest();
            District district = Factory.createDistrict();
            when(districtService.update(district.getId(), request)).thenReturn(district);

            // When
            DistrictResponse response = districtController.update(district.getId(), request);

            // Then
            Assertions.assertNotNull(response);
            Assertions.assertEquals(district.getId(), response.getId());
            Assertions.assertEquals(district.getName(), response.getName());
            Assertions.assertEquals(district.getCity().getId(), response.getCity().getId());
        }
    }

    @Nested
    @DisplayName("Test class for district delete scenarios")
    public class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            // When
            ResponseEntity<Void> response = districtController.delete(district.getId());
            // Then
            Assertions.assertNotNull(response);
            Assertions.assertNull(response.getBody());
            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }
}
