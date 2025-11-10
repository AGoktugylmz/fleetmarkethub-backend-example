package com.cosmosboard.fmh.dto.request.user.autocheck;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateAutoCheckAccountRequest {
    @NotBlank(message = "SID cannot be empty")
    private String sid;
}