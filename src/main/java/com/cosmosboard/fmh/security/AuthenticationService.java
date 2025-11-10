package com.cosmosboard.fmh.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {
    private static final String AUTHORIZATION_FAILED = "Authentication error";

    /**
     * Getting username from the security context
     *
     * @param aInRoles -- roles that user must have
     * @return -- username or null
     * @throws AccessDeniedException -- if user does not have required roles
     */
    public boolean isAuthorized(String... aInRoles) throws AccessDeniedException {
        JwtUserDetails getPrinciple = getPrinciple();
        if (getPrinciple == null) {
            throw new AccessDeniedException(AUTHORIZATION_FAILED);
        }
        try {
            for (String role : aInRoles) {
                for (GrantedAuthority authority : getPrinciple.getAuthorities()) {
                    if (authority.getAuthority().equalsIgnoreCase(role)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new AccessDeniedException(AUTHORIZATION_FAILED);
        }
        return false;
    }

    /**
     * Getting user object that is in the security context
     *
     * @return -- security user object or null
     */
    public JwtUserDetails getPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return (JwtUserDetails) authentication.getPrincipal();
        } catch (Exception e) {
            log.error("Exception while casting principal to JwtUserDetails, Ex: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
