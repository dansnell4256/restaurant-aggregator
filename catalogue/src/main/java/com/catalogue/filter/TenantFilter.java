package com.catalogue.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.common.tenant.TenantContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to extract tenant ID from the request URL and set it in the TenantContextHolder.
 */
@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    private static final Pattern TENANT_PATTERN = Pattern.compile("/api/v1/tenants/([^/]+)");

    private final TenantContextHolder tenantContextHolder;

    /**
     * Constructor.
     *
     * @param tenantContextHolder Holder for tenant context
     */
    public TenantFilter(TenantContextHolder tenantContextHolder) {
        this.tenantContextHolder = tenantContextHolder;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract tenant ID from URL
            String tenantId = extractTenantId(request);

            if (tenantId != null) {
                // Set tenant ID in context
                tenantContextHolder.setTenantId(tenantId);
            }

            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Clear tenant context after request is processed
            tenantContextHolder.clear();
        }
    }

    /**
     * Extract tenant ID from the request URL.
     *
     * @param request HTTP request
     * @return Tenant ID or null if not found
     */
    private String extractTenantId(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Matcher matcher = TENANT_PATTERN.matcher(requestURI);

        String result = null;
        if (matcher.find()) {
            result = matcher.group(1);
        }

        return result;
    }
}
