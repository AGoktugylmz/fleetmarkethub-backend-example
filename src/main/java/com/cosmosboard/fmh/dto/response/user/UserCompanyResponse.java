package com.cosmosboard.fmh.dto.response.user;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.dto.response.company.CompanyResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCompanyResponse extends BaseResponse {
    @Schema(name = "company", description = "company of Offer", nullable = true, type = "CompanyResponse")
    private CompanyResponse company;

    @Schema(name = "isOwner", description = "Is owner or not", type = "Boolean")
    private boolean isOwner;

    public static UserCompanyResponse convert(final CompanyResponse company, final boolean isOwner) {
        return UserCompanyResponse.builder()
            .company(company)
            .isOwner(isOwner)
            .build();
    }
}
