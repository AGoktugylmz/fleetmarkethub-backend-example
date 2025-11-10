package com.cosmosboard.fmh.config;

import com.cosmosboard.fmh.security.JwtAuthenticationEntryPoint;
import com.cosmosboard.fmh.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                //.antMatchers("/v1/admin/**").hasAuthority("ADMIN")
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/v1/auth/**").permitAll()
                .antMatchers("/v1/public/**").permitAll()
                .antMatchers("/v1/media/**").permitAll()
                .antMatchers("/v1/swagger/**").permitAll()
                .antMatchers("/v1/shared/**").permitAll()
                .antMatchers("/v1/webhook/**").permitAll()
                .antMatchers("/swagger**/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/*.html").permitAll()
                .antMatchers("/v1/retail/**").permitAll()
                .antMatchers("/v1/cars/**").permitAll()
                .antMatchers("/v1/outsource/**").permitAll()
                .antMatchers("/v1/transport/**").permitAll()
                .antMatchers(HttpMethod.GET, "/v1/ping").permitAll()
                .antMatchers(
                    "/",
                    "/assets/**",
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/ws/**",
                    "/actuator**/**"
                )
                .permitAll()
                .anyRequest().authenticated()
            .and()
                .headers().frameOptions().disable()
            .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}