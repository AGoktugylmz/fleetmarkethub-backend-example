package com.cosmosboard.fmh.security;

import com.cosmosboard.fmh.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static com.cosmosboard.fmh.util.AppConstants.TOKEN_HEADER;
import static com.cosmosboard.fmh.util.AppConstants.TOKEN_TYPE;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final HashMap<String, List<String>> protectedEndpoints = new HashMap<>();

    public JwtAuthenticationFilter(final JwtTokenProvider jwtTokenProvider, final UserService userService,
                                   final AuthenticationManager authenticationManager,
                                   @Qualifier("requestMappingHandlerMapping") final RequestMappingHandlerMapping requestHandlerMapping) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.authenticationManager = authenticationManager;

        protectedEndpoints.put("GET", new ArrayList<>());
        protectedEndpoints.put("POST", new ArrayList<>());
        protectedEndpoints.put("PUT", new ArrayList<>());
        protectedEndpoints.put("PATCH", new ArrayList<>());
        protectedEndpoints.put("DELETE", new ArrayList<>());

        requestHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            final Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
            for (RequestMethod method : methods) {
                if (handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(Authorize.class) ||
                        handlerMethod.hasMethodAnnotation(Authorize.class) ||
                        handlerMethod.getMethod().getDeclaringClass().getSuperclass().isAnnotationPresent(Authorize.class)) {
                    protectedEndpoints.get(asHttpMethod(method).name())
                            .addAll(requestMappingInfo.getPathPatternsCondition().getPatternValues());
                }
            }
        });
    }

    private HttpMethod asHttpMethod(RequestMethod method) {
        return switch (method) {
            case GET -> HttpMethod.GET;
            case HEAD -> HttpMethod.HEAD;
            case POST -> HttpMethod.POST;
            case PUT -> HttpMethod.PUT;
            case PATCH -> HttpMethod.PATCH;
            case DELETE -> HttpMethod.DELETE;
            case OPTIONS -> HttpMethod.OPTIONS;
            case TRACE -> HttpMethod.TRACE;
        };
    }

    private boolean isProtectedRequest(final HttpServletRequest request) {
        return protectedEndpoints.get(request.getMethod()).stream()
                .anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isProtectedRequest(request)) {
            final String token = extractJwtFromRequest(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, request)) {
                final String id = jwtTokenProvider.getUserIdFromToken(token);
                final UserDetails user = userService.loadUserById(id);
                if (Objects.nonNull(user)) {
                    final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    authenticationManager.authenticate(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extract jwt from request
     *
     * @param request HttpServletRequest object to get Authorization header
     * @return String value of bearer token or null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(String.format("%s ", TOKEN_TYPE)))
            return bearer.substring(TOKEN_TYPE.length() + 1);
        return null;
    }
}
