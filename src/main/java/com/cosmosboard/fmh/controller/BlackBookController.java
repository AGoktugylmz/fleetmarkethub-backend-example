package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.fms.blackbook.search.BlackBookSearchRequest;
import com.cosmosboard.fmh.dto.response.fms.blackbook.search.BlackbookSearchResponse;
import com.cosmosboard.fmh.dto.response.privateMarketValue.PrivateMarketValueResponse;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.security.Subscriber;
import com.cosmosboard.fmh.service.BlackbookCreditService;
import com.cosmosboard.fmh.service.CarService;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.PrivateMarketValueService;
import com.cosmosboard.fmh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/blackbook")
@Authorize
@Subscriber
@Tag(name = "BlackBook", description = "BlackBook API")
@SecurityRequirement(name = "bearerAuth")
public class BlackBookController extends BaseController{

    private final BlackbookCreditService blackbookCreditService;

    private final CarService carService;

    private final MessageSourceService messageSourceService;

    private final UserService userService;

    private final PrivateMarketValueService privateMarketValueService;

    @PostMapping("/search")
    @Operation(summary = "Request Blackbook Search for Vehicles", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = BlackbookSearchResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PrivateMarketValueResponse createBlackbookForSelectedCar(@RequestBody BlackBookSearchRequest request) {
        Company company = getCompanyOfUser();

        Long currentCredits = blackbookCreditService.getNetCreditsByCompanyId(company.getId());

        if (currentCredits == null || currentCredits <= 0) {
            throw new BadRequestException("Your BlackBook credit balance is depleted.");
        }

        Car car = carService.findOneById(request.getCarId());

        return carService.fetchBlackbookValuesForResponse(car, request.getState(), company);
    }

    @GetMapping("/list/{carId}")
    @Operation(summary = "Get Blackbook queries made for a car by the logged-in user's company")
    public List<PrivateMarketValueResponse> getBlackbookQueriesByCar(@PathVariable String carId) {
        String companyId = getCompanyOfUser().getId();
        return privateMarketValueService.getAllByCarIdAndCompany(carId, companyId);
    }

    @GetMapping("/show/{id}")
    @Operation(summary = "Get a single Blackbook query result by ID if it belongs to the logged-in user's company")
    public PrivateMarketValueResponse getBlackbookQueryById(@PathVariable String id) {
        String companyId = getCompanyOfUser().getId();
        return privateMarketValueService.getByIdAndCompany(id, companyId);
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

    private User getUser() {
        return userService.getUser();
    }

}
