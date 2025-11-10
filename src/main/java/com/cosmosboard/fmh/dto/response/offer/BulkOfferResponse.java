package com.cosmosboard.fmh.dto.response.offer;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkOfferResponse extends BaseResponse {

    private List<OfferResponse> offers;

    private List<String> errorMessages;
}
