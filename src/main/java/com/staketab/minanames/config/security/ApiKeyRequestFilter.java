package com.staketab.minanames.config.security;

import com.staketab.minanames.entity.ApiKeyEntity;
import com.staketab.minanames.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

import static com.staketab.minanames.utils.Constants.API_KEY_HEADER;

@Component
@Slf4j
public class ApiKeyRequestFilter extends GenericFilterBean {
    private final ApiKeyRepository apiKeyRepository;
    public static final String API_DOMAINS = "/api/domains";
    public static final String API_ACTIVITIES = "/api/activities";

    public ApiKeyRequestFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String path = req.getRequestURI();
        String key = req.getHeader(API_KEY_HEADER) == null ? "" : req.getHeader(API_KEY_HEADER);

        if (!path.startsWith(API_DOMAINS) && !path.startsWith(API_ACTIVITIES)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        log.debug("Trying authorize with x-api-key: {}", key);
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        Optional<ApiKeyEntity> apiKey = getApiKey(key);
        if (apiKey.isPresent()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            logErrorToUser("Invalid API key", HttpServletResponse.SC_UNAUTHORIZED, resp, servletResponse);
        }
    }

    public Optional<ApiKeyEntity> getApiKey(String apiKey) {
        return apiKeyRepository.findById(apiKey);
    }

    public void logErrorToUser(String txt, int status, HttpServletResponse resp, ServletResponse response) throws IOException {
        resp.reset();
        resp.setStatus(status);
        response.setContentLength(txt.length());
        response.getWriter().write(txt);
    }
}
