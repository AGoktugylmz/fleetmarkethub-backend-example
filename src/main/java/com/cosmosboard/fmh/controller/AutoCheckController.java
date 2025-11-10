package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.user.autocheck.CreateAutoCheckAccountRequest;
import com.cosmosboard.fmh.dto.response.autoCheck.AutoCheckReportResponse;
import com.cosmosboard.fmh.dto.response.autoCheck.AutoCheckResponse;
import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.entity.AutoCheckReport;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.service.AutoCheckService;
import com.cosmosboard.fmh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/auto-check")
@Authorize
@Tag(name = "AutoCheck", description = "AutoCheck API")
@SecurityRequirement(name = "bearerAuth")
public class AutoCheckController extends BaseController {
    private final AutoCheckService autoCheckService;

    private final UserService userService;

    @PostMapping("/create")
    @Operation(
        summary = "Create AutoCheck Account",
        responses = {
            @ApiResponse(responseCode = "201", description = "AutoCheck account created successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    public ResponseEntity<SuccessResponse> createAutoCheckAccount(
            @Valid @RequestBody CreateAutoCheckAccountRequest request
    ) throws Exception {

        User user = getUser();
        autoCheckService.createAutoCheckAccount(user, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.builder().message("Your AutoCheck information has been successfully uploaded.").build());
    }

    @PatchMapping("/update")
    @Operation(
        summary = "Update AutoCheck Account",
        responses = {
            @ApiResponse(responseCode = "200", description = "AutoCheck account updated successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - AutoCheck information does not exist for this user",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    public ResponseEntity<SuccessResponse> updateAutoCheckAccount(
            @Valid @RequestBody CreateAutoCheckAccountRequest request
    ) throws Exception {

        User user = getUser();
        autoCheckService.updateAutoCheckAccount(user, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.builder().message("Your AutoCheck information has been successfully updated.").build());
    }

    @PostMapping("/report/{vin}/{car_id}")
    @Operation(summary = "Request AutoCheck for Car", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = AutoCheckResponse.class))),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AutoCheckResponse> createCarReport(
        @Parameter(description = "Car VIN to request AutoCheck", required = true)
        @PathVariable("vin") String vin,
        @Parameter(description = "Company ID for the report", required = true)
        @PathVariable("car_id") String carID
    ) throws Exception {

        String htmlResponse = autoCheckService.checkCarAutoCheck(vin, getUser(), carID);

        AutoCheckResponse response = AutoCheckResponse.builder()
                .message("AutoCheck request processed successfully")
                .data(htmlResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/{vin}/{car_id}")
    @Operation(summary = "List AutoCheck Reports for Car", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = AutoCheckReportResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No report found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AutoCheckReportResponse>> listAutoCheckReports(
        @Parameter(description = "Car VIN to request AutoCheck", required = true)
        @PathVariable("vin") String vin,
        @Parameter(description = "Car ID", required = true)
        @PathVariable("car_id") String carId) {

        User user = getUser();
        List<AutoCheckReportResponse> reports = autoCheckService.listReportsByVinAndCarId(user.getId(), vin, carId);

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/report/html/{report_id}")
    @Operation(summary = "Get AutoCheck Report HTML by ID", responses = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(mediaType = MediaType.TEXT_HTML_VALUE)),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Report not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AutoCheckResponse> getAutoCheckReportHtml(
        @Parameter(description = "AutoCheck Report ID", required = true)
        @PathVariable("report_id") String reportId) throws IOException {

        User user = getUser();

        AutoCheckReport report = autoCheckService.findById(reportId);

        if (!report.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not allowed to access this report");
        }

        Path filePath = Paths.get("var", "storage", report.getHtmlPath());
        if (!Files.exists(filePath)) {
            throw new NotFoundException("Stored report file not found");
        }

        String htmlContent = Files.readString(filePath);

        AutoCheckResponse response = AutoCheckResponse.builder()
                .message("AutoCheck report loaded successfully")
                .data(htmlContent)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/report/html/{report_id}")
    @Operation(summary = "Delete AutoCheck Report by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Report deleted successfully",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Report not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuccessResponse> deleteAutoCheckReport(
            @Parameter(description = "AutoCheck Report ID", required = true)
            @PathVariable("report_id") String reportId) {

        User user = getUser();

        AutoCheckReport report = autoCheckService.findById(reportId);

        if (!report.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not allowed to delete this report");
        }

        autoCheckService.deleteReport(reportId);

        return ResponseEntity.ok(
                SuccessResponse.builder()
                        .message("Your Report has been successfully deleted.")
                        .build()
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<SuccessResponse> deleteAutoCheckAccount() {
        User user = getUser();
        autoCheckService.deleteAutoCheckAccount(user);

        return ResponseEntity.ok(
                SuccessResponse.builder()
                        .message("Your AutoCheck information has been successfully deleted.")
                        .build()
        );
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
