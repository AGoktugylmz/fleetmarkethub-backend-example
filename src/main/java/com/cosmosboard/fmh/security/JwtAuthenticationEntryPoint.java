package com.cosmosboard.fmh.security;

import com.cosmosboard.fmh.dto.response.ErrorResponse;
import com.cosmosboard.fmh.exception.handler.AppExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    private final AppExceptionHandler appExceptionHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {

        final String expired = (String) request.getAttribute("expired");

        final String unsupported = (String) request.getAttribute("unsupported");

        final String invalid = (String) request.getAttribute("invalid");

        final String illegal = (String) request.getAttribute("illegal");

        final String notfound = (String) request.getAttribute("notfound");

        final String message;

        if (expired != null) {
            message = expired;
        } else if (unsupported != null) {
            message = unsupported;
        } else if (invalid != null) {
            message = invalid;
        } else if (illegal != null) {
            message = illegal;
        } else if (notfound != null) {
            message = notfound;
        } else {
            message = e.getMessage();
        }

        log.error("Could not set user authentication in security context. Error: {}", message);

        ResponseEntity<ErrorResponse> responseEntity = appExceptionHandler.handleBadCredentialsException(
                new BadCredentialsException(message));
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
    }
}
