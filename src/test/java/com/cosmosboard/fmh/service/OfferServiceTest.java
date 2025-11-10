package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.offer.ConfirmOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CreateOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.UpdateOfferRequest;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.OfferFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.OfferRepository;
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
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for OfferService")
public class OfferServiceTest {
    @InjectMocks
    private OfferService offerService;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private CarService carService;

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            when(offerRepository.count()).thenReturn(1L);
            // When
            Long count = offerService.count();
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
            Offer offer = Factory.createOffer();
            List<Offer> offers = List.of(offer);
            when(offerRepository.findAll()).thenReturn(offers);
            // When
            List<Offer> result = offerService.findAll();
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(offer, result.get(0));
            assertEquals(offer.getId(), result.get(0).getId());
            assertEquals(offer.getCreatedAt(), result.get(0).getCreatedAt());
            assertEquals(offer.getUpdatedAt(), result.get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findAllPagination scenarios")
    class FindAllPaginationTest {
        @Test
        void givenOfferCriteriaAndPaginationCriteria_whenFindAllWithPagination_thenAssertBody() {
            // Given
            Offer offer = Factory.createOffer();
            Page<Offer> offers = new PageImpl<>(List.of(offer));
            when(offerRepository.findAll(any(OfferFilterSpecification.class),
                    any(Pageable.class))).thenReturn(offers);
            // When
            Page<Offer> result = offerService.findAll(null,
                    PaginationCriteria.builder().page(1).size(20).build());
            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getContent().size());
            assertEquals(offer, result.getContent().get(0));
            assertEquals(offer.getId(), result.getContent().get(0).getId());
            assertEquals(offer.getCreatedAt(), result.getContent().get(0).getCreatedAt());
            assertEquals(offer.getUpdatedAt(), result.getContent().get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            Offer offer = Factory.createOffer();
            when(offerRepository.findById(any(String.class))).thenReturn(Optional.of(offer));
            // When
            Offer result = offerService.findOneById(offer.getId());
            // Then
            assertNotNull(result);
            assertEquals(offer, result);
            assertEquals(offer.getId(), result.getId());
            assertEquals(offer.getCreatedAt(), result.getCreatedAt());
            assertEquals(offer.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> offerService.findOneById("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("city_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }


    @Nested
    @DisplayName("Test class for findAllByCarIdAndCompanyId scenarios")
    class FindAllByCarIdAndCompanyIdTest {

        @Test
        void givenCarIdAndCompanyId_whenFindAllByCarIdAndCompanyId_thenAssertBody() {
            // Given
            String carId = "car123";
            String companyId = "company456";
            Offer offer = Factory.createOffer();
            when(offerRepository.findAllByCarIdAndCompanyId(carId, companyId))
                    .thenReturn(List.of(offer));

            // When
            List<Offer> result = offerService.findAllByCarIdAndCompanyId(carId, companyId);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(offer, result.get(0));
            assertEquals(offer.getId(), result.get(0).getId());
        }

        @Test
        void givenCarIdAndCompanyId_whenFindAllByCarIdAndCompanyId_thenReturnEmptyList() {
            // Given
            String carId = "car123";
            String companyId = "company456";
            when(offerRepository.findAllByCarIdAndCompanyId(carId, companyId))
                    .thenReturn(Collections.emptyList());

            // When
            List<Offer> result = offerService.findAllByCarIdAndCompanyId(carId, companyId);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenCreateOfferRequest_whenCreate_thenAssertBody() {
            // Given
            CreateOfferRequest request = Factory.createCreateOfferRequest();
            request.setCarId("car123");
            request.setPrice(BigDecimal.valueOf(50000));

            Car car = Factory.createCar();
            Company company = Factory.createCompany();

            when(companyService.findOneById("company123")).thenReturn(company);
            when(offerService.findAllByCarIdAndCompanyId("car123", "company123")).thenReturn(Collections.emptyList());
            when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Offer result = offerService.create(request, car, company);

            // Then
            assertNotNull(result);
            assertEquals(company, result.getCompany());
            assertEquals(car, result.getCar());
            assertEquals(request.getPrice(), result.getPrice());
        }

        @Test
        void givenCreateOfferRequest_whenCreate_thenThrowBadRequestException() {
            // Given
            CreateOfferRequest request = Factory.createCreateOfferRequest();
            request.setCarId("car123");

            Car car = Factory.createCar();
            Company company = Factory.createCompany();
            Offer existingOffer = Factory.createOffer();
            existingOffer.setStatus(AppConstants.OfferStatusEnum.WAITING);

            when(companyService.findOneById("company123")).thenReturn(company);
            when(offerService.findAllByCarIdAndCompanyId("car123", "company123"))
                    .thenReturn(List.of(existingOffer));

            // When
            Executable closureToTest = () -> offerService.create(request, car, company);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("offer_already_progress"), exception.getMessage());
        }

        @Test
        void givenOnlyCancelledOffers_whenCreate_thenAssertBody() {
            // Given
            CreateOfferRequest request = Factory.createCreateOfferRequest();
            request.setCarId("car123");
            request.setPrice(BigDecimal.valueOf(50000));

            Car car = Factory.createCar();
            Company company = Factory.createCompany();
            Offer cancelledOffer = Factory.createOffer();
            cancelledOffer.setStatus(AppConstants.OfferStatusEnum.CANCELLED);

            when(companyService.findOneById("company123")).thenReturn(company);
            when(offerService.findAllByCarIdAndCompanyId("car123", "company123"))
                    .thenReturn(List.of(cancelledOffer));
            when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Offer result = offerService.create(request, car, company);

            // Then
            assertNotNull(result);
            assertEquals(company, result.getCompany());
            assertEquals(car, result.getCar());
            assertEquals(request.getPrice(), result.getPrice());
        }
    }

    @Nested
    @DisplayName("Test class for findCarOneById scenarios")
    class FindCarOneByIdTest {

        @Test
        void givenCarId_whenFindCarOneById_thenAssertBody() {
            // Given
            String carId = "car123";
            Car car = Factory.createCar();
            car.setId(carId);

            when(carService.findOneById(carId)).thenReturn(car);

            // When
            Car result = offerService.findCarOneById(carId);

            // Then
            assertNotNull(result);
            assertEquals(car, result);
            assertEquals(carId, result.getId());
        }

        @Test
        void givenInvalidCarId_whenFindCarOneById_thenThrowNotFoundException() {
            // Given
            String carId = "invalidCarId";
            when(carService.findOneById(carId)).thenThrow(new NotFoundException("Car not found"));

            // When
            Executable closureToTest = () -> offerService.findCarOneById(carId);

            // Then
            NotFoundException exception = assertThrows(NotFoundException.class, closureToTest);
            assertEquals("Car not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenStringAndUpdateOfferRequestAndUser_whenUpdate_thenAssertBody() {
            // Given
            String offerId = "offer123";
            UpdateOfferRequest updateRequest = Factory.createUpdateOfferRequest();
            updateRequest.setCompanyId("companyId");
            updateRequest.setPrice(BigDecimal.valueOf(150.0));
            updateRequest.setStatus(AppConstants.OfferStatusEnum.ACCEPTED);

            User user = Factory.createUser();

            Offer existingOffer = Factory.createOffer();
            existingOffer.setId(offerId);
            when(offerRepository.findById(offerId)).thenReturn(Optional.of(existingOffer));

            Company updatedCompany = Factory.createCompany();
            when(companyService.findOneById(updateRequest.getCompanyId())).thenReturn(updatedCompany);


            Offer savedOffer = Factory.createOffer();
            when(offerService.save(any())).thenReturn(savedOffer);
            when(offerService.update(offerId, updateRequest, user)).thenReturn(existingOffer);

            // When
            Offer result = offerService.update(offerId, updateRequest, user);

            // Then
            assertNotNull(result);
            assertEquals(offerId, result.getId());
            assertEquals(updatedCompany, result.getCompany());
            assertEquals(updateRequest.getPrice(), result.getPrice());
            assertEquals(updateRequest.getStatus(), result.getStatus());
            assertEquals(user, result.getUpdatedBy());
        }
    }

    @Nested
    @DisplayName("Test class for acceptOrReject scenarios")
    class AcceptOrRejectTest {
        @Test
        void givenStringAndConfirmOfferRequest_whenAcceptOrReject_thenAssertBody() {
            // Given
            String offerId = "offer123";
            ConfirmOfferRequest confirmRequest = Factory.createConfirmOfferRequest();
            confirmRequest.setStatus("ACCEPTED");

            Offer existingOffer = Factory.createOffer();
            when(offerRepository.findById(offerId)).thenReturn(Optional.of(existingOffer));

            Offer savedOffer = Factory.createOffer();
            when(offerService.save(any())).thenReturn(savedOffer);
            when(offerService.acceptOrReject(offerId, confirmRequest)).thenReturn(existingOffer);

            // When
            Offer result = offerService.acceptOrReject(offerId, confirmRequest);

            // Then
            assertNotNull(result);
            assertEquals(existingOffer.getId(), result.getId());
            assertEquals(AppConstants.OfferStatusEnum.ACCEPTED, result.getStatus());
        }

    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenString_whenDelete_thenAssertBody() {
            // Given
            String offerId = "offer123";
            Offer existingOffer = Factory.createOffer();

            when(offerRepository.findById(offerId)).thenReturn(Optional.of(existingOffer));

            // When & Then
            assertDoesNotThrow(() -> offerService.delete(offerId));
            verify(offerRepository, times(1)).delete(existingOffer);
        }
    }

    @Nested
    @DisplayName("Test class for save scenarios")
    class SaveTest {
        @Test
        void givenOffer_whenSave_thenAssertBody() {
            // Given
            Offer offerToSave = Factory.createOffer();
            Offer savedOffer = Factory.createOffer();

            when(offerRepository.save(offerToSave)).thenReturn(savedOffer);

            // When
            Offer result = offerService.save(offerToSave);

            // Then
            assertNotNull(result);
            assertEquals(savedOffer, result);
        }

    }
}
