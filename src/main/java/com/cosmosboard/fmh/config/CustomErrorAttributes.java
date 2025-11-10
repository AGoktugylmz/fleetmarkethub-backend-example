package com.cosmosboard.fmh.config;

import com.cosmosboard.fmh.dto.response.ErrorResponse;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import javax.servlet.RequestDispatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class CustomErrorAttributes extends DefaultErrorAttributes {
    private static final String ERROR = "error";

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        Object errorMessage = webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST);

        ErrorResponse build = ErrorResponse.builder().build();
        if (errorMessage != null) {
            build.setStatus((Integer) errorAttributes.get("status"));
            String message = (String) (Objects.nonNull(errorAttributes.get("message")) ? errorAttributes.get("message")
                : Objects.nonNull(errorAttributes.get(ERROR)) ? errorAttributes.get(ERROR) : "Server error");
            build.setMessage(message);
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ERROR, build);
        return map;
    }
}
