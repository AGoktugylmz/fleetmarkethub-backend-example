package com.cosmosboard.fmh.dto.response.transport;

import com.cosmosboard.fmh.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceApiResponse extends BaseResponse {

    @Schema(example = "null", description = "Eğer fiyatlanamıyorsa sebep metni, aksi halde null", name = "aq_reject_reason", nullable = true)
    private String aq_reject_reason;

    @Schema(example = "1037", description = "Base enclosed price", name = "base_enclosed_price", type = "Integer")
    private Integer base_enclosed_price;

    @Schema(example = "11/12/2025", description = "Base estimated delivery (MM/dd/yyyy)", name = "base_estimated_delivery", type = "String")
    private String base_estimated_delivery;

    @Schema(example = "2009", description = "Express enclosed price", name = "express_enclosed_price", type = "Integer")
    private Integer express_enclosed_price;

    @Schema(example = "11/07/2025", description = "Express estimated delivery (MM/dd/yyyy)", name = "express_estimated_delivery", type = "String")
    private String express_estimated_delivery;

    @Schema(example = "972", description = "Express price", name = "express_price", type = "Integer")
    private Integer express_price;

    @Schema(example = "1285", description = "Güzergah mil bilgisi", name = "miles", type = "Integer")
    private Integer miles;

    @Schema(example = "648", description = "Toplam/ana fiyat", name = "price", type = "Integer")
    private Integer price;

    @Schema(example = "6cf33c14-8cda-486b-a0bd-740808bc9ffe",
            description = "Price reference id - sipariş doğrulaması için kullanılır", name = "price_ref_id", type = "String")
    private String price_ref_id;

    @ArraySchema(schema = @Schema(implementation = PriceVehicleResponse.class),
            arraySchema = @Schema(description = "Araç listesi", name = "vehicles", type = "Array"))
    private List<PriceVehicleResponse> vehicles;
}

