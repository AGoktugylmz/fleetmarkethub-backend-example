package com.cosmosboard.fmh.dto.request.car.outsource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedCarStatusResponse {
    private ChangeCarStatusRequest request;

    private String reason;
}