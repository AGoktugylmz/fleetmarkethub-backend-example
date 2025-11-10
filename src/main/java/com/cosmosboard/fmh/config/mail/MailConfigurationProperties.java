package com.cosmosboard.fmh.config.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.mail")
class MailConfigurationProperties {
    private String host;

    private String port;

    private String protocol;

    private String username;

    private String password;
}
