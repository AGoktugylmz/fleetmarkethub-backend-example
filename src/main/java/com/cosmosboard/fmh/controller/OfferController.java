package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.offer.CancelOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.ConfirmOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CreateBulkOfferRequest;
import com.cosmosboard.fmh.dto.request.offer.CreateOfferRequest;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.offer.BulkOfferResponse;
import com.cosmosboard.fmh.dto.response.offer.CancelOfferResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferPaginationResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.OfferCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.service.CompanyService;
import com.cosmosboard.fmh.service.CarService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.NotificationService;
import com.cosmosboard.fmh.service.OfferService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/offers")
@Tag(name = "Offer", description = "Offer API")
@Authorize
@SecurityRequirement(name = "bearerAuth")
@Validated
public class OfferController extends BaseController {
    private static final String[] SORT_COLUMNS = new String[]{"price", "transactionAt", "createdAt", "cancelAt"};

    private static final String USER_NOT_AUTHORIZED_FOR_OFFER = "user_not_authorized_for_offer";

    private static final String CAR_ID = "CAR ID: ";

    private final OfferService offerService;

    private final CompanyService companyService;

    private final MessageSourceService messageSourceService;

    private final UserService userService;

    private final CarService carService;

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List Offers Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = OfferPaginationResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public OfferPaginationResponse list(
        @Parameter(name = "carId", description = "Car ID", example = "0ac365ea-af71-451e-9887-00a4cf94b271")
        @RequestParam(required = false) final String carId,

        @Parameter(name = "statuses", description = "Statuses", example = "ACCEPTED")
        @RequestParam(required = false) final List<AppConstants.OfferStatusEnum> statuses,

        @Parameter(name = "q", description = "Search keyword", example = "lorem")
        @RequestParam(required = false) final String q,

        @Parameter(name = "price", description = "Price", example = "123")
        @RequestParam(required = false) final Double price,

        @Parameter(name = "showMyOffer", description = "Show offers of my company", example = "true")
        @RequestParam(required = false) final Boolean showMyOffer,

        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) final Integer page,

        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) final Integer size,

        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) final String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") final String sort
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            log.error("Invalid Sort Column: {}, should be one of: {}", sortBy, SORT_COLUMNS);
            throw new BadRequestException(messageSourceService.get("invalid_sort_column"));
        }
        if (carId != null) carService.findOneById(carId);
        getCompanyOfUser();
        companyService.findOneById(getCompanyOfUser().getId());

        Page<Offer> offers = offerService.findAll(
                OfferCriteria.builder()
                        .carId(carId)
                        .price(price)
                        .statuses(statuses)
                        .companyId(showMyOffer != null && showMyOffer ? getCompanyOfUser().getId() : null)
                        .carOwnerCompanyId(showMyOffer != null && !showMyOffer ? getCompanyOfUser().getId() : null)
                        .q(q)
                        .build(),
                PaginationCriteria.builder()
                        .page(page)
                        .size(size)
                        .sortBy(sortBy)
                        .sort(sort)
                        .columns(SORT_COLUMNS)
                        .build()
        );
        return new OfferPaginationResponse(offers,
                offers.stream().map(offer -> OfferResponse.convert(offer, true)).toList());
    }

    @PostMapping
    @Operation(summary = "Create Offer Endpoint", responses = {
        @ApiResponse(responseCode = "201", description = "created operation",
            content = @Content(schema = @Schema(hidden = true)),
            headers = @Header(name = "Offer", description = "Offer created",
                schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Authorize(roles = {"USER", "CONSULTANT"})
    public ResponseEntity<OfferResponse> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to create offer", required = true)
        @RequestBody @Validated final CreateOfferRequest request
    ) throws BadRequestException {
        Company company = getCompanyOfUser();
        Car car = offerService.findCarOneById(request.getCarId());
        if (car.getStatus() != AppConstants.CarStatusEnum.APPROVED) {
            String message = messageSourceService.get("offer_not_accepted_for_car_status_not_approved", new String[]{car.getStatus().name()});
            log.error(message);
            throw new BadRequestException(message);
        }
        if (car.getOffers().stream().anyMatch(o -> AppConstants.OfferStatusEnum.ACCEPTED.equals(o.getStatus()) ||
                AppConstants.OfferStatusEnum.FINISHED.equals(o.getStatus()))) {
            String message = messageSourceService.get("offer_not_created_for_one_already_accepted_or_finished");
            log.error(message);
            throw new BadRequestException(message);
        }

        Offer offer = offerService.create(request, car, company);
        return ResponseEntity.status(HttpStatus.CREATED).body(OfferResponse.convert(offer, false));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create Bulk Offers Endpoint", responses = {
        @ApiResponse(responseCode = "201", description = "created operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BulkOfferResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Authorize(roles = {"USER", "CONSULTANT"})
    public ResponseEntity<BulkOfferResponse> createBulk(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to create bulk offers", required = true)
        @RequestBody @Validated final CreateBulkOfferRequest request
    ) throws BadRequestException {
        Company company = getCompanyOfUser();
        List<OfferResponse> offerResponses = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        Set<String> processedCarIds = new HashSet<>();

        for (CreateOfferRequest offerRequest : request.getOffers()) {
            try {
                if (processedCarIds.contains(offerRequest.getCarId())) {
                    errorMessages.add(CAR_ID + offerRequest.getCarId() + " - You have already made an offer for this car.");
                    continue;
                }
                processedCarIds.add(offerRequest.getCarId());

                Car car = offerService.findCarOneById(offerRequest.getCarId());
                if (car.getStatus() != AppConstants.CarStatusEnum.APPROVED) {
                    String message = messageSourceService.get("offer_not_accepted_for_car_status_not_approved", new String[]{car.getStatus().name()});
                    log.error(message);
                    errorMessages.add(CAR_ID + offerRequest.getCarId() + " - " + message);
                    continue;
                }

                if (car.getOffers().stream().anyMatch(o -> o.getCompany().getId().equals(company.getId()) &&
                        (AppConstants.OfferStatusEnum.ACCEPTED.equals(o.getStatus()) || AppConstants.OfferStatusEnum.FINISHED.equals(o.getStatus())))) {
                    String message = messageSourceService.get("offer_not_created_for_one_already_accepted_or_finished");
                    log.error(message);
                    errorMessages.add(CAR_ID + offerRequest.getCarId() + " -- " + message);
                    continue;
                }

                Offer offer = offerService.create(offerRequest, car, company);
                offerResponses.add(OfferResponse.convert(offer, false));

            } catch (BadRequestException e) {
                log.error("Error creating offer for carId: {}", offerRequest.getCarId(), e);
                errorMessages.add(CAR_ID + offerRequest.getCarId() + " - " + e.getMessage());
            }
        }

        BulkOfferResponse response = new BulkOfferResponse(offerResponses, errorMessages);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show Offer Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = OfferResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public OfferResponse show(
        @Parameter(name = "id", description = "Offer ID", required = true)
        @PathVariable("id") final String id
    ) {
        User user = getUser();
        Company userCompany = getCompanyOfUser();

        Offer oneById = offerService.findOneById(id);

        if (oneById.getCompany().getId().equals(userCompany.getId())) {
            return OfferResponse.convert(oneById, true);
        } else {
            String message = messageSourceService.get("user_not_authorized_for_offer", new String[]{user.getId(), id});
            log.error(message);
            throw new AccessDeniedException(message);
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update Offer Status Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully",
            content = @Content(schema = @Schema(implementation = OfferResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied for this offer",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Offer not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public OfferResponse updateStatus(
        @Parameter(name = "id", description = "Offer ID", required = true)
        @PathVariable("id") final String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to Status change offers", required = true)
        @RequestBody @Validated final ConfirmOfferRequest request
    ) {
        User user = getUser();
        Offer offer = offerService.findOneById(id);

        if (!offer.getCarOwnerCompany().getId().equals(getCompanyOfUser().getId())) {
            String message = messageSourceService.get("user_not_authorized_for_offer_update", new String[]{user.getId(), id});
            log.error(message);
            throw new AccessDeniedException(message);
        }

        try {
            AppConstants.OfferStatusEnum statusEnum = AppConstants.OfferStatusEnum.valueOf(request.getStatus().toUpperCase());
            offer.setStatus(statusEnum);

        } catch (IllegalArgumentException e) {
            String message = messageSourceService.get("invalid_offer_status", new String[]{request.getStatus()});
            log.error(message, e);
            throw new BadRequestException(message);
        }

        offer = offerService.save(offer);
        notificationService.sendNotificationUpdateStatus(offer);

        return OfferResponse.convert(offer, true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel Offer Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation"),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Authorize(roles = {"CONSULTANT"})
    public ResponseEntity<CancelOfferResponse> cancel(
        @Parameter(name = "id", description = "Offer ID", required = true) @PathVariable("id") final String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to cancel offer", required = true)
        @RequestBody @Validated final CancelOfferRequest request
    ) {
        User user = getUser();
        Offer offerById = offerService.findOneById(id);

        if (offerById.getCompany().getEmployees().stream().noneMatch(e -> e.getUser() == user)) {
            String message = messageSourceService.get(USER_NOT_AUTHORIZED_FOR_OFFER, new String[]{user.getId(), id});
            log.error(message);
            throw new AccessDeniedException(message);
        }
        if (offerById.getStatus().equals(AppConstants.OfferStatusEnum.CANCELLED)) {
            String message = messageSourceService.get("offer_not_cancelled_again");
            log.error(message);
            throw new BadRequestException(message);
        }

        offerById.setStatus(AppConstants.OfferStatusEnum.CANCELLED);
        offerById.setCancelAt(LocalDateTime.now());
        offerById.setCancelMessage(request.getMessage());
        offerById = offerService.save(offerById);

        Car car = offerById.getCar();
        if (AppConstants.CarStatusEnum.CANCELLED_REQUESTED == car.getStatus()) {
            car.setStatus(AppConstants.CarStatusEnum.CANCELLED);
            carService.save(car);
        }

        String successMessage = messageSourceService.get("offer_cancelled_successfully", new String[]{id});
        CancelOfferResponse response = CancelOfferResponse.builder()
                .offerId(id)
                .status(AppConstants.OfferStatusEnum.CANCELLED.name())
                .message(successMessage)
                .cancelMessage(offerById.getCancelMessage())
                .cancelAt(offerById.getCancelAt())
                .build();

        return ResponseEntity.ok(response);
    }

    private Company getCompanyOfUser() {
        Company company = null;
        for (Employee employee : getUser().getEmployees()) {
            if (employee.getUser().getId().equals(getUser().getId())) {
                company = employee.getCompany();
                break;
            }
        }
        if (company == null) {
            String message = messageSourceService.get("invalid_company");
            log.error(message);
            throw new BadRequestException(message);
        }
        return company;
    }

    /**
     * Get current user
     *
     * @return User entity retrieved from db
     */
    private User getUser() {
        return userService.getUser();
    }
}
