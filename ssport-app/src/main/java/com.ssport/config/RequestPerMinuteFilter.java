package com.ssport.config;

import com.amazonaws.util.json.Jackson;
import com.ssport.exception.ErrorCode;
import com.ssport.exception.SportAppErrorResponse;
import com.ssport.model.users.UserPrincipal;
import com.ssport.service.users.UserAuthFilterService;
import com.ssport.util.Constants;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestPerMinuteFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPerMinuteFilter.class);

    @Autowired
    private UserAuthFilterService filterService;

    @Value("${prymetime.requests-per-minute-limit}")
    private String limitAsString;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean isAllowed;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);

        if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(Constants.ROLE_REGISTERED_USER) || authority.getAuthority().equals(Constants.ROLE_SOCIAL_USER))) {
            isAllowed = filterService.filterByRequestAmount(((UserPrincipal) authentication.getPrincipal()).getUser().getId().toString());
        } else if (authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(Constants.ROLE_GUEST_USER))) {
            isAllowed = filterService.filterByRequestAmount(authentication.getName());
        } else {
            isAllowed = true;
        }
        if (authentication != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("HTTP_REQUEST_LOG: ")
                    .append("\nCURRENT_TIME: ").append(new Date())
                    .append("\nURI").append(request.getRequestURI())
                    .append("\nUSER: ").append(authentication.getName())
                    .append("\nREQUEST_PARAMS: {");
            request.getParameterMap().forEach((key, value) -> builder.append("\n\"").append(key).append("\":\"").append(Arrays.toString(value)).append("\""));
            builder.append("\n}");
            LOGGER.info(builder.toString());
        }

        if (isAllowed) {
            filterChain.doFilter(request, servletResponse);
        } else {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.setContentType("application/json");

            Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

            List<SportAppErrorResponse> errors = new ArrayList<>();
            errors.add(new SportAppErrorResponse(ErrorCode.ACCESS_DENIED, "Too many requests. Try again later"));
            errorResponse.put("errors", errors);

            response.getOutputStream().print(Jackson.toJsonPrettyString(errorResponse));
        }
    }

    @Override
    public void destroy() {

    }
}
