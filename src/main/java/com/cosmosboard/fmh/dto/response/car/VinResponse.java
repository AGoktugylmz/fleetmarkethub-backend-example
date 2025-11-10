package com.cosmosboard.fmh.dto.response.car;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VinResponse extends BaseResponse {
    private VinData data;
}

