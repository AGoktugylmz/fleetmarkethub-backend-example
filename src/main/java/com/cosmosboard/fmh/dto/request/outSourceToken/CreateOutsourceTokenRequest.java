package com.cosmosboard.fmh.dto.request.outSourceToken;

import com.cosmosboard.fmh.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutsourceTokenRequest {
    private String name;

    private Set<AppConstants.OutsourceTokenPermissionEnum> permissions;
}
