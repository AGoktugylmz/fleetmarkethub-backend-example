package com.cosmosboard.fmh.dto.request.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateCompanyRequest {

    @NotBlank(message = "{not_blank}")
    @Schema(example = "Vetfor A.Ş.", description = "name", name = "name", type = "String")
    private String name;

    @NotBlank(message = "{not_blank}")
    @Schema(example = "Bir veteriner hizmetleri şirketidir.", description = "description", name = "description", type = "String")
    private String description;

    @Schema(example = "ddd2edb7-603f-4389-936d-d182beff94ca", description = "Category id", name = "categoryId", type = "String")
    private String categoryId;

    @Schema(example = "false", description = "Is company a dealer with car upload permissions", name = "isDealer", type = "Boolean")
    private Boolean isDealer;
}