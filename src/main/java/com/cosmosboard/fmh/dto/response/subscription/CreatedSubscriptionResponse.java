package com.cosmosboard.fmh.dto.response.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedSubscriptionResponse {
    @Schema(
        name = "subscription",
        description = "Subscription",
        type = "SubscriptionResponse",
        implementation = SubscriptionResponse.class
    )
    private SubscriptionResponse subscription;

    @Schema(
        name = "message",
        description = "Message of the subscription",
        type = "String",
        example = "Subscription created successfully"
    )
    private String message;

    @Schema(
        name = "redirectUrl",
        description = "Redirect URL // if url is not null, then redirect to this url",
        type = "String",
        nullable = true,
        example = "https://checkout.stripe.com/c/pay/..."
    )
    private String redirectUrl;
}
