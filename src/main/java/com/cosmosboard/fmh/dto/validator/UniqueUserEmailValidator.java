package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.UniqueUserEmail;
import com.cosmosboard.fmh.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerMapping;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, String> {
    private final HttpServletRequest request;

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        Map<String, String> uri = (Map<String, String>) request.getAttribute(
            HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (uri != null && uri.get("id") != null) {
            return !userRepository.existsByEmailAndIdNot(email, uri.get("id"));
        }

        return !userRepository.existsByEmail(email);
    }
}
