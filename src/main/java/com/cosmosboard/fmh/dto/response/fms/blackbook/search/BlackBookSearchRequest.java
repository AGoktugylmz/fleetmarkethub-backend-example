package com.cosmosboard.fmh.dto.response.fms.blackbook.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackBookSearchRequest {
    private String carId;

    private String state;
}
