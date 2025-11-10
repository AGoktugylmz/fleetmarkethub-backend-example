package com.cosmosboard.fmh.dto.request.offer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOfferRequest {

    @Schema(example = "message", description = "Cancel status message", name = "message", type = "String")
    private String message;
}
