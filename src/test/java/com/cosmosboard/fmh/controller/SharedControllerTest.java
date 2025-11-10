package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.response.category.CategoryResponse;
import com.cosmosboard.fmh.dto.response.city.CityResponse;
import com.cosmosboard.fmh.dto.response.district.DistrictResponse;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.District;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CategoryService;
import com.cosmosboard.fmh.service.CityService;
import com.cosmosboard.fmh.service.DistrictService;
import com.cosmosboard.fmh.util.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for SharedController")
public class SharedControllerTest {
    @InjectMocks
    private SharedController sharedController;

    @Mock
    private CityService cityService;

    @Mock
    private DistrictService districtService;

    @Mock
    private CategoryService categoryService;

    @Nested
    @DisplayName("Test class for city list scenarios")
    class GetCitiesTest {
        @Test
        void given_whenGetCities_thenAssertBody() {
            // Given
            City city = Factory.createCity();
            when(cityService.findAll()).thenReturn(List.of(city));

            // When
            List<CityResponse> cityResponses = sharedController.getCities();

            // Then
            assertNotNull(cityResponses);
            assertEquals(1, cityResponses.size());
            CityResponse cityResponse = cityResponses.get(0);

            assertEquals(city.getId(), cityResponse.getId());
            assertEquals(city.getName(), cityResponse.getName());
            assertEquals(city.getCode(), cityResponse.getCode());
        }
    }

    @Nested
    @DisplayName("Test class for district list scenarios")
    class GetDistrictsTest {
        @Test
        void givenId_whenGetDistricts_thenAssertBody() {
            // Given
            District district = Factory.createDistrict();
            when(districtService.findAll(district.getCity().getId())).thenReturn(List.of(district));

            // When
            List<DistrictResponse> districtResponses = sharedController.getDistricts(district.getCity().getId());

            // Then
            assertNotNull(districtResponses);
            assertEquals(1, districtResponses.size());
            DistrictResponse districtResponse = districtResponses.get(0);

            assertEquals(district.getId(), districtResponse.getId());
            assertEquals(district.getName(), districtResponse.getName());
        }
    }

    @Nested
    @DisplayName("Test class for category list scenarios")
    class GetCategoriesTest {
        @Test
        void given_whenGetCategories_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryService.findAll(true)).thenReturn(List.of(category));

            // When
            List<CategoryResponse> categoryResponses = sharedController.getCategories();

            // Then
            assertNotNull(categoryResponses);
            assertEquals(1, categoryResponses.size());
            CategoryResponse categoryResponse = categoryResponses.get(0);

            assertEquals(category.getId(), categoryResponse.getId());
            assertEquals(category.getName(), categoryResponse.getName());
            assertEquals(category.getSlug(), categoryResponse.getSlug());
            assertEquals(category.getDescription(), categoryResponse.getDescription());
            assertEquals(category.getIsActive(), categoryResponse.getIsActive());
        }
    }

    @Nested
    @DisplayName("Test class for category show scenarios")
    class ShowCategoryTest {
        @Test
        void givenIdOrSlug_whenShowCategory_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryService.findOneByIdOrSlug(category.getSlug())).thenReturn(category);

            // When
            CategoryResponse categoryResponse = sharedController.showCategory(category.getSlug());

            // Then
            assertNotNull(categoryResponse);
            assertEquals(category.getId(), categoryResponse.getId());
            assertEquals(category.getName(), categoryResponse.getName());
            assertEquals(category.getSlug(), categoryResponse.getSlug());
            assertEquals(category.getDescription(), categoryResponse.getDescription());
            assertEquals(category.getIsActive(), categoryResponse.getIsActive());
        }
    }
    @Nested
    @DisplayName("Test class for category GetEnums scenarios")
    class GetEnumsTest {
        @Test
        void given_whenShowCategory_thenAssertBody() {
            // Given
            AppConstants.EntitySortEnum[] entitySortEnums = AppConstants.EntitySortEnum.values();
            AppConstants.RoleEnum[] roleEnums = AppConstants.RoleEnum.values();
            AppConstants.OfferStatusEnum[] offerStatusEnums = AppConstants.OfferStatusEnum.values();
            AppConstants.OfferConversationStatusEnum[] offerConversationStatusEnums = AppConstants.OfferConversationStatusEnum.values();
            AppConstants.NotificationTypeEnum[] notificationTypeEnums = AppConstants.NotificationTypeEnum.values();
            AppConstants.NotificationStatusEnum[] notificationStatusEnums = AppConstants.NotificationStatusEnum.values();
            AppConstants.CompanyStatusEnum[] companyStatusEnums = AppConstants.CompanyStatusEnum.values();

            // When
            Map<String, Object> responseBody = sharedController.getEnums();

            // Then
            assertNotNull(responseBody);
            assertEquals(entitySortEnums.length, ((AppConstants.EntitySortEnum[]) responseBody.get("EntitySortEnum")).length);
            assertEquals(roleEnums.length, ((AppConstants.RoleEnum[]) responseBody.get("RoleEnum")).length);
            assertEquals(offerStatusEnums.length, ((AppConstants.OfferStatusEnum[]) responseBody.get("OfferStatusEnum")).length);
            assertEquals(offerConversationStatusEnums.length, ((AppConstants.OfferConversationStatusEnum[]) responseBody.get("OfferConversationStatusEnum")).length);
            assertEquals(notificationTypeEnums.length, ((AppConstants.NotificationTypeEnum[]) responseBody.get("NotificationTypeEnum")).length);
            assertEquals(notificationStatusEnums.length, ((AppConstants.NotificationStatusEnum[]) responseBody.get("NotificationStatusEnum")).length);
            assertEquals(companyStatusEnums.length, ((AppConstants.CompanyStatusEnum[]) responseBody.get("CompanyStatusEnum")).length);
        }
    }
}
