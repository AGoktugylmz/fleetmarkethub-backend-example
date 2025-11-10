package com.cosmosboard.fmh.dto.response.offer;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelOfferResponse extends BaseResponse {
    private String offerId;

    private String status;

    private String message;

    private String cancelMessage;

    private LocalDateTime cancelAt;
}
