package com.cosmosboard.fmh.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("error")
public class ErrorResponse extends BaseResponse {
    @Schema(example = "400", description = "Status code", required = true, name = "status", type = "Integer")
    private Integer status;

    @Schema(example = "This is message field", description = "Response messages field", required = true, name = "message", type = "String")
    private String message;

    @ArraySchema(schema = @Schema(example = "Bad Request", description = "Error message", name = "items", type = "Map"))
    private Map<String, String> items;
}
