package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.car.CarPaginationResponse;
import com.cosmosboard.fmh.dto.response.car.CarResponse;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.service.CarService;
import com.cosmosboard.fmh.service.MessageSourceService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Pattern;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/favourite")
@Authorize
@Tag(name = "Favourite", description = "Favourite API")
@SecurityRequirement(name = "bearerAuth")
public class FavouriteController extends BaseController {

    private static final String[] SORT_COLUMNS = new String[]{"id", "name", "address", "createdAt", "updatedAt"};

    private final UserService userService;

    private final CarService carService;

    private final MessageSourceService messageSourceService;

    @PostMapping("/{carId}")
    @Operation(summary = "Add Favorite Endpoint", responses = {
        @ApiResponse(responseCode = "201", description = "Favorite created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = SuccessResponse.class)),
            headers = @Header(name = "Location", description = "Location of created favorite",
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
    public SuccessResponse createFavorite(
        @PathVariable String carId
    ) {
        Car car = carService.findOneById(carId);

        userService.addCarToFavorites(getUser().getId(), car);

        return SuccessResponse.builder()
            .message(String.format("Car with ID %s has been successfully added to favorites.", carId))
            .build();
    }

    @GetMapping
    @Operation(summary = "List Favorites Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "List of favorite cars with pagination",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CarPaginationResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CarPaginationResponse listFavorites(
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

        Page<Car> favoriteCars = userService.findFavoriteCars(
            getUser().getId(),
            PaginationCriteria.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sort(sort)
                .columns(SORT_COLUMNS)
                .build()
        );

        return new CarPaginationResponse(favoriteCars,
                favoriteCars.stream().map(car -> CarResponse.convert(car, true)).toList());
    }

    @DeleteMapping("/{carId}")
    @Operation(summary = "Delete Favorite Endpoint", responses = {
        @ApiResponse(responseCode = "200", description = "Favorite deleted successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse> deleteFavorite(
        @PathVariable String carId
    ) {
        Car car = carService.findOneById(carId);
        userService.removeCarFromFavorites(getUser().getId(), car);

        SuccessResponse response = SuccessResponse.builder()
            .message(String.format("Car with ID %s has been successfully removed from favorites.", carId))
            .build();

        return ResponseEntity.ok(response);
    }

    private User getUser() {
        return userService.getUser();
    }
}
