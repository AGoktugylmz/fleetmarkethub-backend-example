package com.cosmosboard.fmh.dto.request.transport;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePriceRequest {
    @NotBlank
    private String originPostalCode;

    @NotBlank
    private String originState;

    @NotBlank
    private String destinationPostalCode;

    @NotBlank
    private String destinationState;
}