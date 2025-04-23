package com.catalogue.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for execution time measurement.
 * Uses the TimedAspect to intercept method calls and record execution time.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {
    /**
     * The name of the metric.
     *
     * @return The metric name
     */
    String value() default "";

    /**
     * The metric description.
     *
     * @return The description
     */
    String description() default "";

    /**
     * Additional tags to add to the metric.
     *
     * @return Array of tag=value pairs
     */
    String[] extraTags() default {};

    /**
     * Whether to track active requests.
     *
     * @return true to track active requests, false otherwise
     */
    boolean longTask() default false;

    /**
     * Percentiles to calculate.
     *
     * @return Array of percentiles to compute
     */
    double[] percentiles() default {};
}
