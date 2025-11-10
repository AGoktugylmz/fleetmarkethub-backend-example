package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.request.CreateRequestRequest;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.request.RequestPaginationResponse;
import com.cosmosboard.fmh.dto.response.request.RequestResponse;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Request;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.RequestCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.security.Subscriber;
import com.cosmosboard.fmh.service.MessageSourceService;
import com.cosmosboard.fmh.service.NotificationService;
import com.cosmosboard.fmh.service.RequestService;
import com.cosmosboard.fmh.service.UserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Authorize
@RestController
@RequiredArgsConstructor
@Subscriber
@RequestMapping("/v1/request")
@Tag(name = "Request", description = "Request API")
@SecurityRequirement(name = "bearerAuth")
public class RequestController extends BaseController {
    private static final String[] SORT_COLUMNS = new String[]{"id", "name", "code", "createdAt", "updatedAt"};

    private final UserService userService;

    private final RequestService requestService;

    private final MessageSourceService messageSourceService;

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Request List Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Requests List",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RequestPaginationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(hidden = true)))
    })
    public RequestPaginationResponse list(
        @Parameter(name = "q", description = "Search keyword", example = "Sample")
        @RequestParam(required = false) final String q,
        @Parameter(name = "showMyRequests", description = "Show only requests from user's company", example = "false")
        @RequestParam(defaultValue = "false", required = false) final Boolean showMyRequests,
        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(defaultValue = "1", required = false) final Integer page,
        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(defaultValue = "${spring.data.web.pageable.default-page-size}", required = false) final Integer size,
        @Parameter(name = "sortBy", description = "Sort by column", example = "createdAt")
        @RequestParam(defaultValue = "createdAt", required = false) final String sortBy,
        @Parameter(name = "sort", description = "Sort direction", schema = @Schema(type = "string",
            allowableValues = {"asc", "desc"}))
        @RequestParam(defaultValue = "asc", required = false) @Pattern(regexp = "asc|desc") final String sort
    ) {
        if (sortBy != null && !Arrays.asList(SORT_COLUMNS).contains(sortBy)) {
            String message = messageSourceService.get("invalid_sort_column");
            log.error(message);
            throw new BadRequestException(message);
        }

        RequestCriteria requestCriteria = RequestCriteria.builder()
            .q(q)
            .showMyRequests(showMyRequests)
            .company(showMyRequests ? getCompanyOfUser() : null)
            .build();

        Page<Request> requests = requestService.findAll(requestCriteria,
            PaginationCriteria.builder().page(page).size(size).sortBy(sortBy).sort(sort).columns(SORT_COLUMNS).build());

        return new RequestPaginationResponse(requests, requests.stream().map(RequestResponse::convert)
            .collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(summary = "Create Request Endpoint", responses = {
        @ApiResponse(responseCode = "201", description = "Request Created Successfully",
            content = @Content(schema = @Schema(hidden = true)),
            headers = @Header(name = "Location", description = "Location of created request",
                schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access Denied",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "422", description = "Validation Failed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponse createRequest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body to create a new request", required = true)
            @RequestBody @Validated final CreateRequestRequest request
    ) {
        Request createdRequest = requestService.createRequest(request, getUser());
        notificationService.createNotificationWithRequest(createdRequest);

        return RequestResponse.convert(createdRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show Request Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Show Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RequestResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RequestResponse show(
            @Parameter(name = "id", description = "Request ID", required = true)
            @PathVariable("id") final String id
    ) {
        return RequestResponse.convert(requestService.findOneById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Request Endpoint", responses = {
        @ApiResponse(responseCode = "204", description = "Delete Request",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public SuccessResponse delete(
        @Parameter(name = "id", description = "Request ID", required = true)
        @PathVariable("id") final String id
    ) {
        Request request = requestService.findOneById(id);
        Company userCompany = getCompanyOfUser();

        if (!request.getCompany().getId().equals(userCompany.getId())) {
            throw new BadRequestException("You do not have permission to delete this request.");
        }
        requestService.delete(id);
        return SuccessResponse.builder()
            .message("Request with ID " + id + " has been successfully deleted.")
            .build();
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
