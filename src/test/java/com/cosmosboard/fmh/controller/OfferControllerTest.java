package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.offer.AssignOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CancelOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CreateOfferRequest;
import com.cosmosboard.fmh.dto.response.offer.OfferPaginationResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferStatisticsResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.OfferCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CompanyService;
import com.cosmosboard.fmh.service.EmployeeService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.OfferService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for OfferController")

public class OfferControllerTest {
    @InjectMocks
    private OfferController offerController;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private OfferService offerService;

    @Mock
    private UserService userService;

    @Mock
    private CompanyService companyService;

    @Mock
    private EmployeeService employeeService;


    @Nested
    @DisplayName("Test class for offer list scenarios")
    class ListTest {
        @Test
        void givenInvalidSortColumn_whenList_thenBadRequest() {
            // Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("TEST");
            // When
            Executable response = () -> offerController.list(null, null, null, null , null, null, null, "null", null);
            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST", exception.getMessage());
        }
        @Test
        void givenDemandIdAndCompanyIdAndStatusesAndCategoryIdsAndQAndPriceAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            User user = Factory.createUser();
            user.setId("user123");

            List<String> categoryIds = List.of("category1", "category2");
            List<AppConstants.OfferStatusEnum> statuses = List.of(AppConstants.OfferStatusEnum.ACCEPTED);

            List<Offer> mockOfferList = Collections.singletonList(Factory.createOffer());
            Page<Offer> mockOffers = new PageImpl<>(mockOfferList);

            when(userService.getUser()).thenReturn(user);
            when(offerService.findAll(any(OfferCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(mockOffers);

            // When
            OfferPaginationResponse offerPaginationResponse = offerController.list(
                    null, statuses, null, null, null,
                    1, 10, "createdAt", "desc");

            // Then
            assertNotNull(offerPaginationResponse);
            assertEquals(1, offerPaginationResponse.getPage());

            List<OfferResponse> offerResponses = offerPaginationResponse.getItems();
            assertEquals(mockOfferList.size(), offerResponses.size());
        }
    }

    @Nested
    @DisplayName("Test class for offer create scenarios")
    class CreateTest {

        @Test
        void givenCreateOfferRequest_whenCreateWithMismatchedCompanyId_thenBadRequest() {
            // Given
            User testUser = Factory.createUser();

            Employee testEmployee = Factory.createEmployee();
            testEmployee.setOwner(true);

            CreateOfferRequest validRequest = Factory.createCreateOfferRequest();

            when(userService.getUser()).thenReturn(testUser);
            when(employeeService.findByUserAndIsOwner(testUser, true)).thenReturn(testEmployee);
            when(messageSourceService.get("does_not_match_employee_company_id_expected_id"))
                    .thenReturn("TEST");

            // When
            Executable response = () -> offerController.create(validRequest);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST", exception.getMessage());
        }

//        @Test
//        void givenCreateOfferRequest_whenCreateWithUnapprovedDemand_thenBadRequest() {
//            // Given
//            User testUser = Factory.createUser();
//            testUser.setId(UUID.randomUUID().toString());
//
//            Employee testEmployee = Factory.createEmployee();
//            testEmployee.setCompany(Factory.createCompany());
//            testEmployee.getCompany().setId(UUID.randomUUID().toString());
//            testEmployee.setOwner(true);
//
//            Demand testDemand = Factory.createDemand();
//            testDemand.setId(UUID.randomUUID().toString());
//            testDemand.setStatus(AppConstants.DemandStatusEnum.WAITING);
//
//            CreateOfferRequest validRequest = Factory.createCreateOfferRequest();
//            validRequest.setCompanyId(testEmployee.getCompany().getId());
//            validRequest.setDemandId(testDemand.getId());
//
//            when(userService.getUser()).thenReturn(testUser);
//            when(employeeService.findByUserAndIsOwner(testUser, true)).thenReturn(testEmployee);
//            when(offerService.findDemandOneById(testDemand.getId())).thenReturn(testDemand);
//            when(messageSourceService.get("offer_not_accepted_for_demand_status_not_approved", new String[]{"WAITING"}))
//                    .thenReturn("TEST");
//
//            // When
//
//            BadRequestException exception = assertThrows(BadRequestException.class,
//                    () -> offerController.create(validRequest));
//
//            assertEquals("TEST", exception.getMessage());
//        }
//
//        @Test
//        void givenCreateOfferRequest_whenCreateWithOfferAlreadyAcceptedOrFinished_thenBadRequest() {
//            // Given
//            User testUser = Factory.createUser();
//            testUser.setId(UUID.randomUUID().toString());
//
//            Employee testEmployee = Factory.createEmployee();
//            testEmployee.setCompany(Factory.createCompany());
//            testEmployee.getCompany().setId(UUID.randomUUID().toString());
//            testEmployee.setOwner(true);
//
//            Demand testDemand = Factory.createDemand();
//            testDemand.setId(UUID.randomUUID().toString());
//            testDemand.setStatus(AppConstants.DemandStatusEnum.APPROVED);
//
//            Offer finishedOffer = Factory.createOffer();
//            finishedOffer.setStatus(AppConstants.OfferStatusEnum.FINISHED);
//
//            testDemand.setOffers(Collections.singletonList(finishedOffer));
//
//            CreateOfferRequest validRequest = Factory.createCreateOfferRequest();
//            validRequest.setCompanyId(testEmployee.getCompany().getId());
//            validRequest.setDemandId(testDemand.getId());
//
//            when(userService.getUser()).thenReturn(testUser);
//            when(employeeService.findByUserAndIsOwner(testUser, true)).thenReturn(testEmployee);
//            when(offerService.findDemandOneById(testDemand.getId())).thenReturn(testDemand);
//            when(messageSourceService.get("offer_not_created_for_one_already_accepted_or_finished"))
//                    .thenReturn("TEST");
//
//            // When
//            BadRequestException exception = assertThrows(BadRequestException.class,
//                    () -> offerController.create(validRequest));
//
//            // Then
//            assertEquals("TEST", exception.getMessage());
//        }
//
//        @Test
//        void givenCreateOfferRequest_whenCreate_thenAssertBody() {
//            // Given
//            User testUser = Factory.createUser();
//            testUser.setId(UUID.randomUUID().toString());
//
//            Employee testEmployee = Factory.createEmployee();
//            testEmployee.setCompany(Factory.createCompany());
//            testEmployee.getCompany().setId(UUID.randomUUID().toString());
//            testEmployee.setOwner(true);
//
//            Demand testDemand = Factory.createDemand();
//            testDemand.setId(UUID.randomUUID().toString());
//            testDemand.setStatus(AppConstants.DemandStatusEnum.APPROVED);
//            testDemand.getOffers().forEach(o -> o.setStatus(AppConstants.OfferStatusEnum.WAITING));
//
//            CreateOfferRequest validRequest = Factory.createCreateOfferRequest();
//            validRequest.setCompanyId(testEmployee.getCompany().getId());
//            validRequest.setDemandId(testDemand.getId());
//
//            when(userService.getUser()).thenReturn(testUser);
//            when(employeeService.findByUserAndIsOwner(testUser, true)).thenReturn(testEmployee);
//            when(offerService.findDemandOneById(testDemand.getId())).thenReturn(testDemand);
//
//            Offer createdOffer = Factory.createOffer();
//            when(offerService.create(validRequest, testDemand)).thenReturn(createdOffer);
//
//            // When
//            ResponseEntity<OfferResponse> response = offerController.create(validRequest);
//
//            // Then
//            assertEquals(HttpStatus.CREATED, response.getStatusCode());
//            assertNotNull(response.getBody());
//            assertEquals(createdOffer.getId(), response.getBody().getId());
//        }
    }

    @Nested
    @DisplayName("Test class for offer show scenarios")
    class ShowTest{
        @Test
        void givenId_whenShowWithUnauthorizedUser_thenThrowAccessDeniedException() {
            // Given
            String offerId = "valid_offer_id";

            User testUser = Factory.createUser();
            when(userService.getUser()).thenReturn(testUser);

            Offer offer = Factory.createOffer();
            Company mockCompany = Factory.createCompany();

            offer.setCompany(mockCompany);
            when(offerService.findOneById(offerId)).thenReturn(offer);

            assertThrows(AccessDeniedException.class, () -> offerController.show(offerId));
        }

//        @Test
//        void givenId_whenShow_thenAssertBody(){
//            // Given
//            String offerId = "valid_offer_id";
//            User user = Factory.createUser();
//            user.setId("user_id");
//
//            Employee employee = Factory.createEmployee();
//            employee.setUser(user);
//
//            Company mockCompany = Factory.createCompany();
//            mockCompany.setId("company_id");
//            mockCompany.setEmployees(List.of(employee));
//
//            Offer mockOffer = Factory.createOffer();
//            mockOffer.setCompany(mockCompany);
//
//            when(userService.getUser()).thenReturn(user);
//            when(offerService.findOneById(offerId)).thenReturn(mockOffer);
//
//            // When
//            OfferResponse response = offerController.show(offerId);
//
//            // Then
//            assertNotNull(response);
//        }
    }

    @Nested
    @DisplayName("Test class for offer cancel scenarios")
    class CancelTest{

        @Test
        void givenIdAndCancelOfferRequest_whenCancelWithUnauthorizedUser_thenAccessDenied() {
            // Given
            String offerId = "valid_offer_id";
            User testUser = Factory.createUser();
            testUser.setId(UUID.randomUUID().toString());

            Offer mockOffer = Factory.createOffer();
            Company mockCompany = Factory.createCompany();

            mockOffer.setCompany(mockCompany);
            mockCompany.setEmployees(Collections.emptyList());

            when(userService.getUser()).thenReturn(testUser);
            when(offerService.findOneById(offerId)).thenReturn(mockOffer);
            when(messageSourceService.get("user_not_authorized_for_offer", new String[]{testUser.getId(), offerId}))
                    .thenReturn("TEST");

            CancelOfferRequest validRequest = Factory.createCancelOfferRequest();

            // When
            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> offerController.cancel(offerId, validRequest));

            // Then
            assertEquals("TEST", exception.getMessage());
        }

        @Test
        void givenIdAndCancelOfferRequest_whenCancelWithAlreadyCancelledOffer_thenBadRequest() {
            // Given
            String offerId = "valid_offer_id";
            User testUser = Factory.createUser();
            testUser.setId(UUID.randomUUID().toString());

            Offer mockOffer = Factory.createOffer();
            Company mockCompany = Factory.createCompany();
            Employee testEmployee = Factory.createEmployee();

            mockOffer.setCompany(mockCompany);
            mockCompany.setEmployees(Collections.singletonList(testEmployee));
            testEmployee.setUser(testUser);

            mockOffer.setStatus(AppConstants.OfferStatusEnum.CANCELLED);

            when(userService.getUser()).thenReturn(testUser);
            when(offerService.findOneById(offerId)).thenReturn(mockOffer);
            when(messageSourceService.get("offer_not_cancelled_again")).thenReturn("TEST");

            CancelOfferRequest validRequest = Factory.createCancelOfferRequest();

            // When
            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> offerController.cancel(offerId, validRequest));

            // Then
            assertEquals("TEST", exception.getMessage());
        }

//        @Test
//        void givenIdAndCancelOfferRequest_whenCancel_thenAssertBody() {
//            // Given
//            String offerId = "valid_offer_id";
//            User testUser = Factory.createUser();
//            testUser.setId(UUID.randomUUID().toString());
//
//            Offer offer = Factory.createOffer();
//            Company mockCompany = Factory.createCompany();
//            Employee testEmployee = Factory.createEmployee();
//
//            offer.setCompany(mockCompany);
//            mockCompany.setEmployees(List.of(testEmployee));
//            testEmployee.setUser(testUser);
//
//            offer.setStatus(AppConstants.OfferStatusEnum.WAITING);
//
//            when(userService.getUser()).thenReturn(testUser);
//            when(offerService.findOneById(offerId)).thenReturn(offer);
//            when(offerService.save(offer)).thenReturn(offer);
//
//            CancelOfferRequest validRequest = Factory.createCancelOfferRequest();
//
//            // When
//            ResponseEntity<Void> response = offerController.cancel(offerId, validRequest);
//
//            // Then
//            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//            assertNotNull(offer.getCancelAt());
//            assertEquals(validRequest.getMessage(), offer.getCancelMessage());
//        }

    }

//    @Nested
//    @DisplayName("Test class for offer statistics scenarios")
//    class GetOfferStatisticsTest {
//        @Test
//        void given_whenGetOfferStatisticsWithUserWithoutPermission_thenBadRequest() {
//            // Given
//            User user = Factory.createUser();
//            user.setEmployees(new ArrayList<>());
//
//            when(userService.getUser()).thenReturn(user);
//            when(messageSourceService.get("user_not_permission_see_statistics")).thenReturn("TEST");
//
//            // When & Then
//            BadRequestException exception = assertThrows(
//                    BadRequestException.class,
//                    () -> offerController.getOfferStatistics(3)
//            );
//
//            assertEquals("TEST", exception.getMessage());
//        }
//
//        @Test
//        void given_whenGetOfferStatistics_thenAssertBody() {
//            // Given
//            int maxCategories = 3;
//
//            User user = Factory.createUser();
//            Employee ownerEmployee = Factory.createEmployee();
//            ownerEmployee.setOwner(true);
//            Company company = Factory.createCompany();
//            ownerEmployee.setCompany(company);
//            user.setEmployees(List.of(ownerEmployee));
//
//            OfferStatisticsResponse expectedResponse = Factory.createOfferStatisticsResponse();
//            when(offerService.calculateOfferStatistics(company, maxCategories)).thenReturn(expectedResponse);
//            when(userService.getUser()).thenReturn(user);
//
//            // When
//            OfferStatisticsResponse result = offerController.getOfferStatistics(maxCategories);
//
//            // Then
//            assertEquals(expectedResponse, result);
//            assertNotNull(result);
//            assertEquals(expectedResponse, result);
//            verify(offerService, times(1)).calculateOfferStatistics(company, maxCategories);
//            verify(messageSourceService, never()).get(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test class for offer Update scenarios")
//    class UpdateOfferTest {
//
//        @Test
//        void givenOfferIdAndAssignOfferRequest_whenUpdateOfferWithAssignOperation_thenAssignedOfferReturned() {
//            // Given
//            Offer offer = Factory.createOffer();
//            User user = Factory.createUser();
//            AssignOfferRequest request = Factory.createAssignOfferRequest();
//            request.setOperation("ASSIGN");
//
//            OfferResponse expectedResponse = Factory.createOfferResponse();
//
//            when(offerService.findOneById(offer.getId())).thenReturn(offer);
//            when(userService.getUser()).thenReturn(user);
//            when(offerService.assignOfferToEmployee(request, user, offer)).thenReturn(expectedResponse);
//
//            // When
//            OfferResponse response = offerController.updateOffer(offer.getId(), request);
//
//            // Then
//            assertNotNull(response);
//            assertEquals(expectedResponse, response);
//        }
//        @Test
//        void givenOfferIdAndAssignOfferRequest_whenUpdateOfferWithUnAssignOperation_thenUnAssignedOfferReturned() {
//            // Given
//            Offer offer = Factory.createOffer();
//            User user = Factory.createUser();
//            AssignOfferRequest request = Factory.createAssignOfferRequest();
//            request.setOperation("UNASSIGN");
//
//            OfferResponse expectedResponse = Factory.createOfferResponse();
//
//            when(offerService.findOneById(offer.getId())).thenReturn(offer);
//            when(userService.getUser()).thenReturn(user);
//            when(offerService.unAssignToOffer(request, user, offer)).thenReturn(expectedResponse);
//
//            // When
//            OfferResponse response = offerController.updateOffer(offer.getId(), request);
//
//            // Then
//            assertNotNull(response);
//            assertEquals(expectedResponse, response);
//        }
//
//        @Test
//        void givenOfferIdAndAssignOfferRequest_whenUpdateOfferWithUpdatedOperation_thenUpdatedOfferReturned() {
//            // Given
//            Offer offer = Factory.createOffer();
//            User user = Factory.createUser();
//            AssignOfferRequest request = Factory.createAssignOfferRequest();
//            request.setOperation("UPDATE");
//
//            OfferResponse expectedResponse = Factory.createOfferResponse();
//
//            when(offerService.findOneById(offer.getId())).thenReturn(offer);
//            when(userService.getUser()).thenReturn(user);
//            when(offerService.updateToOffer(request, user, offer)).thenReturn(expectedResponse);
//
//            // When
//            OfferResponse response = offerController.updateOffer(offer.getId(), request);
//
//            // Then
//            assertNotNull(response);
//            assertEquals(expectedResponse, response);
//        }
//    }

}



