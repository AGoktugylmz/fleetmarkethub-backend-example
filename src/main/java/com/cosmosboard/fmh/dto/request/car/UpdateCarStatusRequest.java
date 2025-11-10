package com.cosmosboard.fmh.dto.request.car;

import com.cosmosboard.fmh.dto.IResource;
import com.cosmosboard.fmh.util.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCarStatusRequest implements IResource<UpdateCarStatusRequest> {

    @Schema(example = "APPROVED", description = "status of the car", name = "status", title = "Status")
    private AppConstants.CarStatusEnum status;
}
