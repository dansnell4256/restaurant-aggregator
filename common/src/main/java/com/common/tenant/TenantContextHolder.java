package com.common.tenant;

import org.springframework.stereotype.Component;

/**
 * Utility class to store and retrieve the current tenant ID in a ThreadLocal.
 */
@Component
public class TenantContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    /**
     * Set the tenant ID for the current thread
     */
    public void setTenantId(String tenantId) {
        CONTEXT.set(tenantId);
    }

    /**
     * Get the tenant ID for the current thread
     */
    public String getTenantId() {
        return CONTEXT.get();
    }

    /**
     * Clear the tenant ID for the current thread
     */
    public void clear() {
        CONTEXT.remove();
    }
}
