package com.cosmosboard.fmh.dto.response.fms;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class ApiResponse {
    private boolean success;

    private List<String> data;
}