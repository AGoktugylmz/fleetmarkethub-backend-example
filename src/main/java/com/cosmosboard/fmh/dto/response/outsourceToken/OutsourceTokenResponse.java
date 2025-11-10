package com.cosmosboard.fmh.dto.response.outsourceToken;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import com.cosmosboard.fmh.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutsourceTokenResponse extends BaseResponse {
    private String name;

    private String token;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<AppConstants.OutsourceTokenPermissionEnum> permissions;
}
