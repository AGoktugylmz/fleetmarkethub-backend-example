package com.cosmosboard.fmh.dto.response.autoCheck;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class AutoCheckResponse extends BaseResponse {
    @Builder.Default
    @Schema(example = "200", description = "Response status code", required = true, name = "status", type = "Integer")
    private Integer status = HttpStatus.OK.value();

    @Schema(example = "Message is here", description = "Response message field", required = true, name = "message", type = "String")
    private String message;

    @Schema(example = "Response data is here", description = "Generic data field", name = "data", type = "Object")
    @JsonRawValue
    private Object data;
}
