package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.car.CreateCarRequestExcel;
import com.cosmosboard.fmh.dto.request.car.outsource.BatchChangeCarStatusResponse;
import com.cosmosboard.fmh.dto.request.car.outsource.ChangeCarStatusRequest;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.car.BatchCarResponse;
import com.cosmosboard.fmh.dto.response.car.CarPaginationResponse;
import com.cosmosboard.fmh.dto.response.car.CarResponse;
import com.cosmosboard.fmh.dto.response.car.FailedCarResponse;
import com.cosmosboard.fmh.dto.response.marketValue.MarketValueResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.dto.response.outsourceToken.OutsourceTokenResponse;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.MarketValue;
import com.cosmosboard.fmh.entity.OutsourceToken;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.CarCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.security.OutsourceTokenCheck;
import com.cosmosboard.fmh.service.CarService;
import com.cosmosboard.fmh.service.CategoryService;
import com.cosmosboard.fmh.service.CompanyService;
import com.cosmosboard.fmh.service.MarketValueService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.OutsourceTokenService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
import com.cosmosboard.fmh.util.swagger.GenericErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/outsource")
@Tag(name = "Outsource", description = "Outsource API")
public class OutsourceController extends BaseController {
    private static final String[] SORT_COLUMNS = new String[]{"id", "title", "content", "createdAt", "updatedAt", "mileage", "defaultMarketValue", "modelYear"};

    private static final String INVALID_SORT_COLUMN = "invalid_sort_column";

    private static final String COMPANY = "company";

    private final CarService carService;

    private final CategoryService categoryService;

    private final MessageSourceService messageSourceService;

    private final CompanyService companyService;

    private final UserService userService;

    private final MarketValueService marketValueService;

    private final OutsourceTokenService outsourceTokenService;

    @Autowired private HttpServletRequest request;

    @GetMapping("/tokens")
    @Authorize(roles = {"CONSULTANT"}, isOwer = true)
    @Operation(summary = "List all OutsourceTokens of a Company", responses = {
        @ApiResponse(responseCode = "200", description = "Tokens listed",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OutsourceToken.class)))),
        @ApiResponse(responseCode = "404", description = "Company not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<OutsourceTokenResponse> listOutsourceTokens(
    ) {
        return outsourceTokenService.findAllByCompanyId(getCompanyOfUser().getId());
    }

    @OutsourceTokenCheck(permissions = {AppConstants.OutsourceTokenPermissionEnum.CAR_LIST})
    @GetMapping
    @Operation(summary = "Car Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CarPaginationResponse.class)))
        }
    )
    @GenericErrorResponse
    public CarPaginationResponse list(
        @Parameter(name = "companyId", description = "Company ID", example = "0ac365ea-af71-451e-9887-00a4cf94b271")
        @RequestParam(required = false) String companyId,

        @Parameter(name = "categoryIds", description = "Category IDs", example = "3d3c2604-ff5d-417a-b0f4-757b0d4c2554")
        @RequestParam(required = false) List<String> categoryIds,

        @Parameter(name = "locationIds", description = "Location IDs", example = "123e4567-e89b-12d3-a456-426614174000")
        @RequestParam(required = false) List<String> locationIds,

        @Parameter(name = "title", description = "Title", example = "Lorem")
        @RequestParam(required = false) String title,

        @Parameter(name = "content", description = "Content", example = "Lorem")
        @RequestParam(required = false) String content,

        @Parameter(name = "brandName", description = "Brand Name", example = "Audi")
        @RequestParam(required = false) String brandName,

        @Parameter(name = "createdAtStart", description = "Created date start", example = "2022-10-25T22:54:58")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtStart,

        @Parameter(name = "createdAtEnd", description = "Created date end", example = "2022-10-25T22:54:58")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtEnd,

        @Parameter(name = "statuses", description = "Statuses", example = "WAITING,APPROVED")
        @RequestParam(required = false) List<String> statuses,

        @Parameter(name = "model", description = "Model of the car", example = "0ac365ea-af71-451e-9887-00a4cf94b271")
        @RequestParam(required = false) String modelId,

        @Parameter(name = "vin", description = "vin", example = "123132312")
        @RequestParam(required = false) String vin,

        @Parameter(name = "unit", description = "unit", example = "Lorem")
        @RequestParam(required = false) String unit,

        @Parameter(name = "modelYearStart", description = "Model year start", example = "2014")
        @RequestParam(required = false) Integer modelYearStart,

        @Parameter(name = "modelYearEnd", description = "Model year end", example = "2024")
        @RequestParam(required = false) Integer modelYearEnd,

        @Parameter(name = "priceStart", description = "Minimum price", example = "10000")
        @RequestParam(required = false) Float priceStart,

        @Parameter(name = "priceEnd", description = "Maximum price", example = "50000")
        @RequestParam(required = false) Float priceEnd,

        @Parameter(name = "mileageStart", description = "Minimum mileage", example = "5000")
        @RequestParam(required = false) Integer mileageStart,

        @Parameter(name = "mileageEnd", description = "Maximum mileage", example = "150000")
        @RequestParam(required = false) Integer mileageEnd,

        @Parameter(name = "exteriorColor", description = "exteriorColor", example = "red")
        @RequestParam(required = false) String exteriorColor,

        @Parameter(name = "interiorColor", description = "interiorColor", example = "red")
        @RequestParam(required = false) String interiorColor,

        @Parameter(name = "conditionStart", description = "Minimum condition value", example = "0.5")
        @RequestParam(required = false) Float conditionStart,

        @Parameter(name = "conditionEnd", description = "Maximum condition value", example = "2.5")
        @RequestParam(required = false) Float conditionEnd,

        @Parameter(name = "carGroupName", description = "Car Group Name", example = "SUV")
        @RequestParam(required = false) String carGroupName,

        @Parameter(name = "retail", description = "Filter by retail price availability", example = "true")
        @RequestParam(required = false) Boolean retail,

        @Parameter(name = "defaultMarketValue", description = "Filter by wholesale price availability", example = "true")
        @RequestParam(required = false) Boolean defaultMarketValue,

        @Parameter(name = "q", description = "Search keyword", example = "lorem")
        @RequestParam(required = false) String q,

        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) Integer page,

        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) Integer size,

        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") String sort,

        @Parameter(name = "showMyFleet", description = "show only my fleet", example = "true")
        @RequestParam(required = false, defaultValue = "false") boolean showMyFleet,

        @Parameter(name = "export", description = "Export as Excel", example = "true")
        @RequestParam(required = false, defaultValue = "false") boolean export,
        HttpServletResponse response
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            String message = messageSourceService.get(INVALID_SORT_COLUMN);
            log.error(message);
            throw new BadRequestException(message);
        }
        if (categoryIds != null && !categoryIds.isEmpty())
            categoryIds.forEach(categoryService::findOneByIdAndActive);
        if (showMyFleet) {
            Company company = (Company) request.getAttribute(COMPANY);
            companyId = company.getId();
        } else if (companyId != null)
            companyService.findOneById(companyId);
        if (modelId != null)
            carService.findCarModelOneById(modelId);

        CarCriteria carCriteria = CarCriteria.builder()
                .companyId(companyId)
                .categoryIds(categoryIds)
                .title(title)
                .content(content)
                .statuses(statuses != null ? statuses.stream().map(AppConstants.CarStatusEnum::getByName).toList() : null)
                .modelId(modelId)
                .brandName(brandName)
                .vin(vin)
                .unit(unit)
                .modelYearStart(modelYearStart)
                .modelYearEnd(modelYearEnd)
                .priceStart(priceStart)
                .priceEnd(priceEnd)
                .mileageStart(mileageStart)
                .mileageEnd(mileageEnd)
                .exteriorColor(exteriorColor)
                .interiorColor(interiorColor)
                .retail(retail)
                .defaultMarketValue(defaultMarketValue)
                .conditionStart(conditionStart)
                .conditionEnd(conditionEnd)
                .createdAtStart(createdAtStart)
                .createdAtEnd(createdAtEnd)
                .q(q)
                .locationIds(locationIds)
                .carGroupName(carGroupName)
                .build();

        Page<Car> cars = carService.findAll(
                carCriteria,
                PaginationCriteria.builder()
                        .page(page)
                        .size(size)
                        .sortBy(sortBy)
                        .sort(sort)
                        .columns(SORT_COLUMNS)
                        .build()
        );

        if (cars.isEmpty()) {
            log.info("No cars found for the given criteria, returning empty response.");
            return new CarPaginationResponse(Page.empty(), Collections.emptyList());
        }

        List<String> carIds = cars.getContent().stream().map(Car::getId).toList();
        Map<String, List<MarketValue>> marketValuesByCarId = marketValueService.findLastMarketValuesForCars(carIds)
                .stream()
                .collect(Collectors.groupingBy(mv -> mv.getCar().getId()));

        List<CarResponse> carResponses = cars.stream().map(car -> {
            final CarResponse carResponse = CarResponse.convert(car, true);

            carResponse.setOffers(car.getOffers().stream().map(offer -> {
                final OfferResponse offerResponse = OfferResponse.convert(offer);
                offerResponse.setCar(null);
                offerResponse.setUpdatedBy(null);
                if (offerResponse.getCompany() != null) {
                    offerResponse.getCompany().setEmployees(null);
                }
                return offerResponse;
            }).toList());

            List<MarketValue> marketValues = marketValuesByCarId.getOrDefault(car.getId(), Collections.emptyList());
            if (!marketValues.isEmpty()) {
                carResponse.setLastMarketValues(marketValues.stream()
                        .collect(Collectors.toMap(
                                mv -> mv.getProvider().name().toLowerCase(),
                                MarketValueResponse::convert
                        )));
            }

            return carResponse;
        }).toList();
        return new CarPaginationResponse(cars, carResponses);
    }

    @OutsourceTokenCheck(permissions = {AppConstants.OutsourceTokenPermissionEnum.CAR_ADD})
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchCarResponse createFromExcel(@RequestBody List<CreateCarRequestExcel> requests) {
        List<CarResponse> carResponses = new ArrayList<>();
        List<FailedCarResponse> failedCars = new ArrayList<>();
        List<Car> createdCars = new ArrayList<>();
        Company company = (Company) request.getAttribute(COMPANY);

        requests.forEach(request -> {
            try {
                request.validate();
                Car car = carService.createExcel(company, request);
                carResponses.add(CarResponse.convert(car, true));

                createdCars.add(car);
            } catch (Exception e) {
                failedCars.add(new FailedCarResponse(request, e.getMessage()));
            }
        });
        createdCars.forEach(carService::fetchMarketValuesForCar);
        return new BatchCarResponse(carResponses, failedCars);
    }

    @OutsourceTokenCheck(permissions = {AppConstants.OutsourceTokenPermissionEnum.CAR_UPDATE})
    @PostMapping("/update/status/batch")
    @ResponseStatus(HttpStatus.OK)
    public BatchChangeCarStatusResponse changeCarStatusBatch(
            @RequestBody List<ChangeCarStatusRequest> requests,
            HttpServletRequest request) {

        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("En az bir güncelleme isteği gönderilmelidir.");
        }

        Company company = (Company) request.getAttribute("company");

        return carService.changeCarStatusBatch(company, requests);
    }

    private User getUser() {
        return userService.getUser();
    }

    private Company getCompanyOfUser() {
        final User user = getUser();
        Company company = null;
        for (Employee employee : user.getEmployees()) {
            if (employee.getUser().getId().equals(user.getId())) {
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
}
