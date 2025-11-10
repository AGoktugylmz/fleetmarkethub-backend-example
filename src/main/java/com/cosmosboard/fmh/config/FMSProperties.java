package com.cosmosboard.fmh.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "fleetvaluationsystem")
public class FMSProperties {
    private String base;

    private String apiKey;
}
