package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.car.CreateCarRequest;
import com.cosmosboard.fmh.dto.request.car.CreateCarRequestExcel;
import com.cosmosboard.fmh.dto.request.car.ParseExcelResult;
import com.cosmosboard.fmh.dto.request.car.PreviewCarRequest;
import com.cosmosboard.fmh.dto.request.car.UpdateCarRequest;
import com.cosmosboard.fmh.dto.request.car.UpdateCarStatusRequest;
import com.cosmosboard.fmh.dto.request.fms.manheim.ManheimSearchRequest;
import com.cosmosboard.fmh.dto.request.fms.manheim.ManheimVehicleSearchItem;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.car.BatchCarResponse;
import com.cosmosboard.fmh.dto.response.car.BatchCarWithExcelResponse;
import com.cosmosboard.fmh.dto.response.car.CarPaginationResponse;
import com.cosmosboard.fmh.dto.response.car.CarResponse;
import com.cosmosboard.fmh.dto.response.car.FailedCarResponse;
import com.cosmosboard.fmh.dto.response.car.VinMmrResponse;
import com.cosmosboard.fmh.dto.response.car.brand.CarBrandNameResponse;
import com.cosmosboard.fmh.dto.response.car.cclass.CarClassPaginationResponse;
import com.cosmosboard.fmh.dto.response.car.cclass.CarClassResponse;
import com.cosmosboard.fmh.dto.response.car.group.CarGroupPaginationResponse;
import com.cosmosboard.fmh.dto.response.car.group.CarGroupResponse;
import com.cosmosboard.fmh.dto.response.car.model.CarModelNameResponse;
import com.cosmosboard.fmh.dto.response.car.trim.CarModelTrimNameResponse;
import com.cosmosboard.fmh.dto.response.car.update.BatchCarStatusUpdateResponse;
import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchResponse;
import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchVehicleData;
import com.cosmosboard.fmh.dto.response.fms.manheim.search.ManheimSearchVehicleItem;
import com.cosmosboard.fmh.dto.response.image.ImageResponse;
import com.cosmosboard.fmh.dto.response.marketValue.MarketValueResponse;
import com.cosmosboard.fmh.dto.response.offer.OfferResponse;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.CarBrand;
import com.cosmosboard.fmh.entity.CarClass;
import com.cosmosboard.fmh.entity.CarGroup;
import com.cosmosboard.fmh.entity.CarModel;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Image;
import com.cosmosboard.fmh.entity.MarketValue;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.CarCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.security.Subscriber;
import com.cosmosboard.fmh.service.CategoryService;
import com.cosmosboard.fmh.service.CompanyService;
import com.cosmosboard.fmh.service.CarService;
import com.cosmosboard.fmh.service.EmployeeService;
import com.cosmosboard.fmh.service.FMSService;
import com.cosmosboard.fmh.service.ImageService;
import com.cosmosboard.fmh.service.MarketValueService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.NotificationService;
import com.cosmosboard.fmh.service.UserService;
import com.cosmosboard.fmh.util.AppConstants;
import com.cosmosboard.fmh.util.swagger.GenericErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Subscriber
@RequestMapping("/v1/cars")
@Authorize
@Tag(name = "Car", description = "Car API")
@SecurityRequirement(name = "bearerAuth")
public class CarController extends BaseController {
    private static final String[] SORT_COLUMNS = new String[]{"id", "title", "content", "createdAt", "updatedAt", "mileage", "defaultMarketValue", "modelYear"};

    private static final String INVALID_SORT_COLUMN = "invalid_sort_column";

    private static final String REGION = "NA";

    private static final Integer GRADE_MULTIPLIER = 10;

    private final UserService userService;

    private final CarService carService;

    private final CategoryService categoryService;

    private final MessageSourceService messageSourceService;

    private final CompanyService companyService;

    private final FMSService FMSService;

    private final EmployeeService employeeService;

    private final ImageService imageService;

    private final NotificationService notificationService;

    private final MarketValueService marketValueService;

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
            final Company company = getCompanyOfUser();
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
                .excludeUserRoleCompanies(true)
                .build();

        if (export) {
            List<Car> cars = carService.findAll(carCriteria);
            List<String> carIds = cars.stream().map(Car::getId).toList();

            Map<String, List<MarketValue>> marketValuesByCarId = marketValueService.findLastMarketValuesForCars(carIds)
                    .stream()
                    .collect(Collectors.groupingBy(mv -> mv.getCar().getId()));

            cars.forEach(car -> {
                List<MarketValue> marketValues = marketValuesByCarId.getOrDefault(car.getId(), Collections.emptyList());
                car.setMarketValues(marketValues);
            });

            carService.exportCarsToExcel(cars, response);
            return null;
        }

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

    @GetMapping("/brands/{year}")
    @Operation(summary = "Car Brands Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = List.class)))
        }
    )
    @GenericErrorResponse
    public List<CarBrandNameResponse> listBrands(
        @Parameter(name = "year", description = "Year for fetching brands", example = "2023") @PathVariable int year,

        @Parameter(name = "name", description = "name", example = "Lorem") @RequestParam(required = false) String name,

        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema =
        @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "desc"))

        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") String sort
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            String message = messageSourceService.get(INVALID_SORT_COLUMN);
            log.error(message);
            throw new BadRequestException(message);
        }
        final Page<CarBrand> brands = carService.findAllBrands(
            carService.toPredicateCarBrand(name),
            PaginationCriteria.builder()
                .page(1)
                .size(Integer.MAX_VALUE)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );
        final List<CarBrandNameResponse> list = brands.stream()
            .map(brand -> new CarBrandNameResponse(brand.getName()))
            .collect(Collectors.toList());
        FMSService
            .fetchVehicleBrandsByYear(year)
            .forEach(brandName -> list.add(new CarBrandNameResponse(brandName)));
        return list;
    }

    @GetMapping("/models/{year}/{brand}")
    @Operation(summary = "Car Models Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = List.class)))
        }
    )
    @GenericErrorResponse
    public List<CarModelNameResponse> listModels(
        @Parameter(name = "year", description = "Year for fetching models", example = "2023") @PathVariable int year,

        @Parameter(name = "brand", description = "Brand name", example = "Audi") @PathVariable String brand,

        @RequestParam(required = false) String name, @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") String sort
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            String message = messageSourceService.get(INVALID_SORT_COLUMN);
            log.error(message);
            throw new BadRequestException(message);
        }
        final Page<CarModel> models = carService.findAllModels(
            carService.toPredicateCarModel(name, brand, null, null),
            PaginationCriteria.builder()
                .page(1)
                .size(Integer.MAX_VALUE)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );
        final List<CarModelNameResponse> list = models.stream()
            .map(model -> new CarModelNameResponse(model.getName()))
            .collect(Collectors.toList());
        FMSService
            .fetchVehicleModelsByYearAndBrand(year, brand)
            .forEach(modelName -> list.add(new CarModelNameResponse(modelName)));
        return list;
    }

    @GetMapping("/trim/{year}/{brand}/{model}")
    public List<CarModelTrimNameResponse> listModelDetails(
        @Parameter(name = "year", description = "Year for fetching models", example = "2023") @PathVariable int year,

        @Parameter(name = "brand", description = "Brand name", example = "Audi") @PathVariable String brand,

        @Parameter(name = "model", description = "Model name", example = "A8") @PathVariable String model,

        @Parameter(name = "name", description = "Name") @RequestParam(required = false) String name,

        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") String sort
    ) {
        final Page<CarModel> models = carService.findAllModels(
            carService.toPredicateCarModel(name, brand, null, null),
            PaginationCriteria.builder()
                .page(1)
                .size(Integer.MAX_VALUE)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );
        final List<CarModelTrimNameResponse> list = models.stream()
            .map(modelEntity -> new CarModelTrimNameResponse(modelEntity.getName()))
            .collect(Collectors.toList());
        FMSService
            .fetchVehicleModelDetailsByYearBrandAndModel(year, brand, model)
            .forEach(detail -> list.add(new CarModelTrimNameResponse(detail)));
        return list;
    }

    @GetMapping("groups")
    @Operation(summary = "Car Groups Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CarGroupPaginationResponse.class)))
        }
    )
    @GenericErrorResponse
    public CarGroupPaginationResponse listGroups(
        @Parameter(name = "name", description = "name", example = "Lorem")
        @RequestParam(required = false) String name,

        @Parameter(name = "modelsIds", description = "Models IDs", example = "0ac365ea-af71-451e-9887-00a4cf94b271")
        @RequestParam(required = false) List<String> modelsIds,

        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) Integer page,

        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) Integer size,

        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") String sort
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            String message = messageSourceService.get(INVALID_SORT_COLUMN);
            log.error(message);
            throw new BadRequestException(message);
        }
        final Page<CarGroup> groups = carService.findAllGroups(
            carService.toPredicateCarGroup(name, modelsIds),
            PaginationCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );
        final List<CarGroupResponse> list = groups.stream().map(CarGroupResponse::convert).toList();
        return new CarGroupPaginationResponse(groups, list);
    }

    @GetMapping("classes")
    @Operation(summary = "Car Class Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CarClassPaginationResponse.class)))
        }
    )
    @GenericErrorResponse
    public CarClassPaginationResponse listClasses(
        @Parameter(name = "name", description = "name", example = "Lorem")
        @RequestParam(required = false) String name,

        @Parameter(name = "modelsIds", description = "Models IDs", example = "0ac365ea-af71-451e-9887-00a4cf94b271")
        @RequestParam(required = false) List<String> modelsIds,

        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) Integer page,

        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) Integer size,

        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) String sortBy,

        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        @RequestParam(defaultValue = "desc", required = false) @Pattern(regexp = "asc|desc") String sort
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            String message = messageSourceService.get(INVALID_SORT_COLUMN);
            log.error(message);
            throw new BadRequestException(message);
        }
        final Page<CarClass> classes = carService.findAllClasses(
            carService.toPredicateCarClass(name, modelsIds),
            PaginationCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );
        final List<CarClassResponse> list = classes.stream().map(CarClassResponse::convert).toList();
        return new CarClassPaginationResponse(classes, list);
    }

    @PostMapping("/vin/{vin}")
    public ResponseEntity<String> provisionVin(
        @Parameter(name = "vin", description = "Vehicle Identification Number (VIN)", example = "3N1CN8EVXRL828710")
        @PathVariable String vin
    ) {
        try {
            String response = FMSService.provisionVin(Map.of("vin", vin, "region", REGION));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error occurred while processing the VIN.");
        }
    }

    @PostMapping("/preview")
    @Operation(summary = "Preview single car MMR", responses = {
        @ApiResponse(responseCode = "200", description = "MMR value for the given VIN",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VinMmrResponse.class)))
    })
    public PreviewCarRequest previewSingle(@RequestBody PreviewCarRequest request) {
        request.validate();

        BigDecimal mmrValue = null;
        try {
            ManheimSearchResponse results = FMSService.manheimSearch(
                ManheimSearchRequest.builder()
                    .vehicles(List.of(ManheimVehicleSearchItem.builder()
                        .vin(request.getVin())
                        .mileage(request.getMileage())
                        .grade(request.getCondition() != null ? (int) (request.getCondition() * GRADE_MULTIPLIER) : null)
                        .region(request.getRegionType().name())
                        .build()))
                    .build()
            );

            if (results.getData() != null && !results.getData().isEmpty()) {
                ManheimSearchVehicleData vd = results.getData().get(0);
                if (vd.getItems() != null && !vd.getItems().isEmpty()) {
                    ManheimSearchVehicleItem best = vd.getItems().stream()
                            .filter(ManheimSearchVehicleItem::getBestMatch)
                            .findFirst()
                            .orElse(vd.getItems().get(0));

                    mmrValue = best.getAuction().getGood();
                }
            }
        } catch (Exception e) {
            log.error("Error fetching MMR for preview VIN {}: {}", request.getVin(), e.getMessage());
        }

        request.setMmr(mmrValue);
        return request;
    }

    @PostMapping
    @Operation(summary = "Create car Endpoint",
        responses = {
            @ApiResponse(responseCode = "201", description = "created operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CarResponse.class))),
        }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to create car", required = true)
        @RequestBody final CreateCarRequest createCarRequest
    ) throws BadRequestException, JsonProcessingException {
        createCarRequest.validate();
        final Car car = carService.create(getCompanyOfUser(), createCarRequest);
        carService.fetchMarketValuesForCar(car);
        return CarResponse.convert(car, true);
    }

    @PostMapping("/file")
    @Operation(summary = "Create cars from Excel", responses = {
        @ApiResponse(responseCode = "201", description = "Batch car creation operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BatchCarWithExcelResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public BatchCarWithExcelResponse previewFromExcel(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty.");
        }

        ParseExcelResult result = carService.parseExcelFile(file);
        List<CreateCarRequestExcel> successfulCars = result.getSuccessfulCars();
        List<FailedCarResponse> failedCars = result.getFailedCars();

        List<ManheimVehicleSearchItem> vehicles = successfulCars.stream()
            .map(car -> ManheimVehicleSearchItem.builder()
                .vin(car.getVin())
                .mileage(car.getMileage())
                .grade(car.getCondition() != null ? (int) (car.getCondition() * GRADE_MULTIPLIER) : null)
                .region(car.getRegionType().name())
                .build())
            .collect(Collectors.toList());

        try {
            ManheimSearchResponse results = FMSService.manheimSearch(
                    ManheimSearchRequest.builder()
                            .vehicles(vehicles)
                            .build()
            );

            Map<String, BigDecimal> mmrMap = results.getData().stream()
                .filter(vehicle -> vehicle.getItems() != null && !vehicle.getItems().isEmpty())
                .collect(Collectors.toMap(
                    vehicle -> vehicle.getVehicle().getVin(),
                    vehicle -> vehicle.getItems().stream()
                        .filter(ManheimSearchVehicleItem::getBestMatch)
                        .findFirst()
                        .orElse(vehicle.getItems().get(0))
                        .getAuction()
                        .getGood()
                    ));

            successfulCars.forEach(car -> car.setMmr(mmrMap.get(car.getVin())));

        } catch (Exception e) {
            log.error("Error fetching MMR values: {}", e.getMessage());
        }

        return new BatchCarWithExcelResponse(successfulCars, failedCars);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchCarResponse createFromExcel(@RequestBody List<CreateCarRequestExcel> requests) {
        List<CarResponse> carResponses = new ArrayList<>();
        List<FailedCarResponse> failedCars = new ArrayList<>();

        List<Car> createdCars = new ArrayList<>();

        requests.forEach(request -> {
            try {
                request.validate();
                Car car = carService.createExcel(getCompanyOfUser(), request);
                carResponses.add(CarResponse.convert(car, true));

                createdCars.add(car);
            } catch (Exception e) {
                failedCars.add(new FailedCarResponse(request, e.getMessage()));
            }
        });

        createdCars.forEach(carService::fetchMarketValuesForCar);

        return new BatchCarResponse(carResponses, failedCars);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show car Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = CarResponse.class))),
    })
    @GenericErrorResponse
    public CarResponse show(@Parameter(name = "id", description = "car ID", required = true) @PathVariable final String id) {
        Car car = carService.findOneById(id);
        CarResponse response = CarResponse.convert(car, true);

        response.setOffers(car.getOffers().stream().map(offer -> {
            final OfferResponse offerResponse = OfferResponse.convert(offer);
            offerResponse.setCar(null);
            offerResponse.setUpdatedBy(null);
            if (offerResponse.getCompany() != null) {
                offerResponse.getCompany().setEmployees(null);
            }
            return offerResponse;
        }).toList());

        List<MarketValue> marketValues = marketValueService.findLastMarketValuesForCar(car.getId());
        if (!marketValues.isEmpty()) {
            response.setLastMarketValues(marketValues.stream()
                .collect(Collectors.toMap(
                    marketValue -> marketValue.getProvider().name().toLowerCase(),
                    MarketValueResponse::convert
                )));
        }
        return response;
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update car Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Update car",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CarResponse.class))),
    })
    @GenericErrorResponse
    public CarResponse update(
        @Parameter(name = "id", description = "car ID", required = true) @PathVariable final String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to car update", required = true)
        @RequestBody final UpdateCarRequest request
    ) {
        Float oldDefaultMarketValue = checkUserAndCarRelationAndGet(id).getDefaultMarketValue();
        Car updatedCar = carService.update(id, request);

        Float newDefaultMarketValue = updatedCar.getDefaultMarketValue();

        if ((oldDefaultMarketValue != null && !oldDefaultMarketValue.equals(newDefaultMarketValue)) ||
                (oldDefaultMarketValue == null && newDefaultMarketValue != null)) {
            notificationService.createNotificationWithCarPriceChange(oldDefaultMarketValue, updatedCar);
        }
        return CarResponse.convert(updatedCar, true);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Car Status Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "Updated operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CarResponse.class))),
            @ApiResponse(responseCode = "404", description = "Car not found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        }
    )
    public CarResponse updateStatus(
        @Parameter(name = "id", description = "car ID", required = true) @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to update car status", required = true)
        @RequestBody final UpdateCarStatusRequest updateCarStatusRequest
    ) {
        final Car updatedCar = carService.updateStatus(id, updateCarStatusRequest.getStatus());
        return CarResponse.convert(updatedCar, true);
    }

    @PatchMapping("/bulk-status-update")
    @Operation(summary = "Bulk update car status via Excel")
    @GenericErrorResponse
    public ResponseEntity<BatchCarStatusUpdateResponse> updateCarStatusesFromExcel(
        @RequestPart("file") MultipartFile file,
        @RequestParam("status") AppConstants.CarStatusEnum status
    ) {
        Company company = getCompanyOfUser();
        BatchCarStatusUpdateResponse response = carService.processBulkCarStatusUpdate(file, status, company);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Car deleted successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Car not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GenericErrorResponse
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse delete(
        @Parameter(name = "id", description = "car ID", required = true) @PathVariable("id") final String id
    ) {
        carService.delete(checkUserAndCarRelationAndGet(id));
        return SuccessResponse.builder()
            .message("Car with ID " + id + " has been successfully deleted.")
            .build();
    }

    @PatchMapping("/image/{carId}")
    @Operation(summary = "Update Car Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse updateImageCar(
        @Parameter(description = "Car ID to update", required = true)
        @PathVariable("carId") String carId,
        @Parameter(description = "Avatar image files", required = true)
        @RequestPart("avatars") List<MultipartFile> avatars
    ) {
        User user = getUser();
        String companyId = employeeService.getCompanyIdByUserId(user.getId());

        checkCarOwnership(carId, companyId);

        for (MultipartFile avatar : avatars) {
            carService.updateImage(carId, avatar);
        }

        return SuccessResponse.builder()
            .message(messageSourceService.get("car_updated_successfully"))
            .build();
    }

    @GetMapping("/image/list/{carId}")
    @Operation(summary = "Get All Images of a Car", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Car not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ImageResponse> getImagesForCar(
        @Parameter(description = "Car ID to get images", required = true)
        @PathVariable("carId") String carId
    ) {
        User user = getUser();
        String companyId = employeeService.getCompanyIdByUserId(user.getId());

        checkCarOwnership(carId, companyId);

        List<Image> images = imageService.getImagesByCarId(carId);

        if (images.isEmpty()) {
            throw new BadRequestException("No images found for Car ID " + carId);
        }

        return images.stream().map(ImageResponse::convert).toList();
    }

    @GetMapping("/image/show/{imageId}")
    @Operation(summary = "Get a single Image by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = Image.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Image not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Image getImageById(
        @Parameter(description = "Image ID to get", required = true)
        @PathVariable("imageId") String imageId
    ) {
        Optional<Image> imageOptional = imageService.getImageById(imageId);

        if (imageOptional.isEmpty()) {
            throw new BadRequestException("Image ID " + imageId + " not found.");
        }

        return imageOptional.get();
    }

    @DeleteMapping("/image/{carId}/{imageId}")
    @Operation(summary = "Delete an Image by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Image not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse deleteImageById(
        @Parameter(description = "Car ID to check ownership", required = true)
        @PathVariable("carId") String carId,
        @Parameter(description = "Image ID to delete", required = true)
        @PathVariable("imageId") String imageId
    ) {
        User user = getUser();

        String companyId = employeeService.getCompanyIdByUserId(user.getId());

        checkCarOwnership(carId, companyId);
        Optional<Image> imageOptional = imageService.getImageById(imageId);

        if (imageOptional.isEmpty()) {
            throw new BadRequestException("Image ID " + imageId + " not found.");
        }

        imageService.deleteImage(imageId);

        return SuccessResponse.builder()
            .message(messageSourceService.get("image_deleted_successfully"))
            .build();
    }

    /**
     * Retrieve a car from db and check whether it belongs to jwt user or not
     *
     * @param id car id
     * @return car entity
     */
    private Car checkUserAndCarRelationAndGet(final String id) {
        final Car car = carService.findOneByIdAndCompanyId(id, getCompanyOfUser().getId());
        car.getOffers().forEach(offer -> {
            if (offer.getStatus().equals(AppConstants.OfferStatusEnum.ACCEPTED) ||
                offer.getStatus().equals(AppConstants.OfferStatusEnum.FINISHED)) {
                String message = messageSourceService.get("cannot_update_car_because_offer_not_valid",
                    new String[]{car.getId(), offer.getStatus().name()});
                log.error(message);
                throw new BadRequestException(message);
            }
        });
        return car;
    }

    private CarPaginationResponse createPaginationResponse(final Page<Car> cars) {
        if (cars.isEmpty()) {
            log.info("No cars found for the given criteria, returning empty response.");
            return new CarPaginationResponse(Page.empty(), Collections.emptyList());
        }

        return new CarPaginationResponse(cars, cars.stream().map(car -> {
            final CarResponse carResponse = CarResponse.convert(car, true);
            carResponse.setOffers(car.getOffers().stream().map(offer -> {
                final OfferResponse offerResponse = OfferResponse.convert(offer);
                offerResponse.setCar(null);
                offerResponse.setUpdatedBy(null);
                if (offerResponse.getCompany() != null)
                    offerResponse.getCompany().setEmployees(null);
                return offerResponse;
            }).toList());

            List<MarketValue> marketValues = marketValueService.findLastMarketValuesForCar(car.getId());
            if (!marketValues.isEmpty()) {
                carResponse.setLastMarketValues(marketValues.stream()
                    .collect(Collectors.toMap(
                        marketValue -> marketValue.getProvider().name().toLowerCase(),
                        MarketValueResponse::convert
                    )));
            }

            return carResponse;
        }).toList());
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

    private void checkCarOwnership(String carId, String companyId) {
        if (!carService.isCarRelatedToCompany(carId, companyId)) {
            throw new BadRequestException("Car ID " + carId + " is not associated with your company.");
        }
    }
}