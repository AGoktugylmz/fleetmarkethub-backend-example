package com.cosmosboard.fmh.dto.response.user;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AvatarResponse extends BaseResponse {
    @Schema(example = "lipsum.jpg", description = "Response avatar field", required = true, name = "avatar",
        type = "String")
    private String avatar;
}
