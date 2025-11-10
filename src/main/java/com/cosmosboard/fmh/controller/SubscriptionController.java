package com.cosmosboard.fmh.controller;

import com.cosmosboard.fmh.dto.request.subscription.CreateSubscriptionRequest;
import com.cosmosboard.fmh.dto.response.subscription.CreatedSubscriptionResponse;
import com.cosmosboard.fmh.dto.response.subscription.SubscriptionResponse;
import com.cosmosboard.fmh.security.Authorize;
import com.cosmosboard.fmh.service.SubscriptionService;
import com.cosmosboard.fmh.util.swagger.GenericErrorResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/subscription")
@Authorize
@Tag(name = "Subscription", description = "Subscription API")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    @Operation(summary = "Get Subscription Status Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SubscriptionResponse.class)))
        }
    )
    @GenericErrorResponse
    public ResponseEntity<SubscriptionResponse> status() {
        return new ResponseEntity<>(subscriptionService.status(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create Subscription Endpoint",
        responses = {
            @ApiResponse(responseCode = "201", description = "Successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreatedSubscriptionResponse.class)))
        }
    )
    @GenericErrorResponse
    public ResponseEntity<CreatedSubscriptionResponse> create(
        @Parameter(description = "Request body to create or resume subscription", required = true)
        @RequestBody final CreateSubscriptionRequest request
    ) {
        return new ResponseEntity<>(subscriptionService.create(request), HttpStatus.CREATED);
    }

    @PatchMapping
    @Operation(summary = "Update Subscription Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreatedSubscriptionResponse.class)))
        }
    )
    @GenericErrorResponse
    public ResponseEntity<CreatedSubscriptionResponse> update(
        @Parameter(description = "Request body to create or resume subscription", required = true)
        @RequestBody final CreateSubscriptionRequest request
    ) {
        return new ResponseEntity<>(subscriptionService.update(request), HttpStatus.OK);
    }

    @PostMapping("/purchase-blackbook-credits")
    public ResponseEntity<CreatedSubscriptionResponse> purchaseBlackbookCredits() {
        return new ResponseEntity<>(subscriptionService.purchaseBlackbookCredits(), HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Cancel Subscription Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreatedSubscriptionResponse.class)))
        }
    )
    @GenericErrorResponse
    public ResponseEntity<CreatedSubscriptionResponse> cancel() {
        return new ResponseEntity<>(subscriptionService.cancel(), HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Resume Subscription Endpoint",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreatedSubscriptionResponse.class)))
        }
    )
    @GenericErrorResponse
    public ResponseEntity<CreatedSubscriptionResponse> resume() {
        return new ResponseEntity<>(subscriptionService.resume(), HttpStatus.OK);
    }
}
