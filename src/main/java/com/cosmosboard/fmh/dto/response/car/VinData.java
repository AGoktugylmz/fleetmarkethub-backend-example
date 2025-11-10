package com.cosmosboard.fmh.dto.response.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VinData {

    private String brand;

    private String model;

    @JsonProperty("model_detail")
    private String modelDetail;

    @JsonProperty("model_year")
    private int modelYear;
}
