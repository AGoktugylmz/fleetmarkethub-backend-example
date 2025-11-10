package com.cosmosboard.fmh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import java.util.List;
import static com.cosmosboard.fmh.config.AppConfig.SECURITY_SCHEME_NAME;

@Configuration
public class SwaggerConfig {
    @Bean
    public ModelResolver modelResolver(final ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name}") final String title, @Value("${server.port}") final String port) {
        final SecurityScheme securitySchemesItem = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .description("JWT auth description")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .in(SecurityScheme.In.HEADER)
                .bearerFormat("JWT");
        final Info license = new Info().title(title).version("1.0")
                .description(title)
                .termsOfService("https://github.com/senocak")
                .license(new License().name("Apache 2.0").url("https://springdoc.org"));
        final Server server1 = new Server().url("http://localhost:"+port).description("Local Server");
        final Server server2 = new Server().url("https://fmh-api.cosmosboard.com/").description("Test Server");
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securitySchemesItem))
                .info(license)
                .servers(List.of(server1, server2));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder().displayName("Admin operations").group("admin").pathsToMatch("/v1/admin/**").build();
    }

    @Bean
    public GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder().displayName("Account operations").group("account").pathsToMatch("/v1/account/**").build();
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder().displayName("Metric operations").group("actuator").pathsToMatch("/actuator/**").build();
    }

    @Bean
    public GroupedOpenApi addressApi() {
        return GroupedOpenApi.builder().displayName("Address operations").group("address").pathsToMatch("/v1/addresses/**").build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder().displayName("Auth operations").group("auth").pathsToMatch("/v1/auth/**").build();
    }

    @Bean
    public GroupedOpenApi companiesApi() {
        return GroupedOpenApi.builder().displayName("Company operations").group("companies").pathsToMatch("/v1/companies/**").build();
    }

    @Bean
    public GroupedOpenApi carApi() {
        return GroupedOpenApi.builder().displayName("Car operations").group("cars").pathsToMatch("/v1/cars/**").build();
    }

    @Bean
    public GroupedOpenApi employeesApi() {
        return GroupedOpenApi.builder().displayName("Employee operations").group("employees").pathsToMatch("/v1/employees/**").build();
    }

    @Bean
    public GroupedOpenApi mediaApi() {
        return GroupedOpenApi.builder().displayName("Media operations").group("media").pathsToMatch("/v1/media/**").build();
    }

    @Bean
    public GroupedOpenApi offerApi() {
        return GroupedOpenApi.builder().displayName("Offer operations").group("offer").pathsToMatch("/v1/offers/**").build();
    }

    @Bean
    public GroupedOpenApi subscriptionApi() {
        return GroupedOpenApi.builder().displayName("Subscription operations").group("subscription").pathsToMatch("/v1/subscription/**").build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().displayName("Public operations").group("shared").pathsToMatch("/v1/shared/**").build();
    }
}
