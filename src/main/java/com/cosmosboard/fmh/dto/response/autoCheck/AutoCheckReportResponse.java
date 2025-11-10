package com.cosmosboard.fmh.dto.response.autoCheck;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AutoCheckReportResponse {
    private String id;

    private String vin;

    private String htmlPath;

    private String carId;

    private Long createdAt;
}