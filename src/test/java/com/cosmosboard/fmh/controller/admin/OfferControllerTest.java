package com.cosmosboard.fmh.controller.admin;

import com.cosmosboard.fmh.dto.request.offer.UpdateOfferRequest;
import com.cosmosboard.fmh.dto.response.offer.OfferPaginationResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Location;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.OfferCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.EmailService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.OfferService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for OfferControllerTest")
public class OfferControllerTest {
    @InjectMocks
    private OfferController offerController;

    @Mock
    private OfferService offerService;

    @Mock
    private UserService userService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private EmailService emailService;


    @Nested
    @DisplayName("Test class for Offer list scenarios")
    public class ListTest {
        @Test
        void givenDemandIdAndCompanyIdAndStatusesAndCategoryIdsAndQAndPriceAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenThrowBadRequestException() {
            // Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("dummy");
            // When
            Executable closureToTest = () -> offerController.list(null, null,
                    null, null, null, null, null,
                    "InvalidSortBy", null);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
        }

        @Test
        void givenDemandIdAndCompanyIdAndStatusesAndCategoryIdsAndQAndPriceAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            Offer offer = Factory.createOffer();
            Page<Offer> page = new PageImpl<>(List.of(offer));
            when(offerService.findAll(any(OfferCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(page);
            List<AppConstants.OfferStatusEnum> statuses = List.of(AppConstants.OfferStatusEnum.ACCEPTED , AppConstants.OfferStatusEnum.WAITING);

            // When
            OfferPaginationResponse offerResponse = offerController.list(null, null,
                    statuses, "q", null, 1, 20, "createdAt", "desc");

            // Then
            Assertions.assertNotNull(offerResponse);
            assertEquals(1, offerResponse.getPage());
            assertEquals(1, offerResponse.getPages());
            assertEquals(1, offerResponse.getTotal());
            assertEquals(1, offerResponse.getItems().size());

            OfferResponse offerItemResponse = offerResponse.getItems().get(0);
            assertEquals(offer.getId(), offerItemResponse.getId());
        }
    }

    @Nested
    @DisplayName("Test class for Offer show scenarios")
    public class ShowTest {
        @Test
        void givenId_whenShow_thenAssertBody() {
            // Given
            Offer offer = Factory.createOffer();
            Company company = Factory.createCompany();
            Location location = Factory.createLocation();
            Car car = Factory.createCar();
            company.setLocations(List.of(location));
            City city = Factory.createCity();
            location.setCity(city);

            car.setCompany(company);
            car.setLocation(location);
            car.setOffers(List.of(offer));
            when(offerService.findOneById(offer.getId())).thenReturn(offer);

            // When
            OfferResponse response = offerController.show(offer.getId());

            // Then
            Assertions.assertNotNull(response);
            assertEquals(offer.getId(), response.getId());
        }
    }

    @Nested
    @DisplayName("Test class for Offer update scenarios")
    public class UpdateTest {
        @Test
        void givenIdAndUpdateOfferRequest_whenUpdate_thenAssertBody() {
            // Given
            Offer offer = Factory.createOffer();
            UpdateOfferRequest request = Factory.createUpdateOfferRequest();
            User user = Factory.createUser();
            offer.setStatus(AppConstants.OfferStatusEnum.ACCEPTED);

            when(userService.getUser()).thenReturn(user);
            when(offerService.update(offer.getId(), request, user)).thenReturn(offer);

            // When
            OfferResponse response = offerController.update(offer.getId(), request);

            // Then
            Assertions.assertNotNull(response);
            assertEquals(offer.getId(), response.getId());
        }
    }

    @Nested
    @DisplayName("Test class for Offer delete scenarios")
    public class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            Offer offer = Factory.createOffer();
            // When
            ResponseEntity<Void> response = offerController.delete(offer.getId());
            // Then
            Assertions.assertNotNull(response);
            Assertions.assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }
}
