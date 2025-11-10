package com.cosmosboard.fmh.dto.request.car.outsource;

import com.cosmosboard.fmh.dto.IResource;
import com.cosmosboard.fmh.util.AppConstants;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChangeCarStatusRequest implements IResource<ChangeCarStatusRequest> {
    @NotBlank
    private String vin;

    @NotNull
    private AppConstants.CarStatusEnum status;
}
