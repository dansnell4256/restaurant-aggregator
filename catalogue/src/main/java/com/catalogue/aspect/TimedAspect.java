package com.catalogue.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.catalogue.annotation.Timed;

/**
 * Aspect that handles the custom @Timed annotation for performance monitoring.
 */
@Aspect
@Component
public class TimedAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimedAspect.class);

    /**
     * Advice that measures and logs the execution time of methods
     * annotated with @Timed.
     *
     * @param joinPoint The joint point for advice
     * @return The result of the method execution
     * @throws Throwable If the method execution throws any exception or error
     */
    @Around("@annotation(com.catalogue.annotation.Timed)")
    public Object timeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get method signature and extract annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Timed timedAnnotation = signature.getMethod().getAnnotation(Timed.class);

        // Get metric name from annotation or use method name
        String metricName = timedAnnotation.value().isEmpty()
                ? signature.getDeclaringType().getSimpleName() + "." + signature.getName()
                : timedAnnotation.value();

        // Record start time
        long startTime = System.currentTimeMillis();

        try {
            // Execute the method
            Object result = joinPoint.proceed();

            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;

            // Log execution time
            LOGGER.info("Method {} executed in {} ms", metricName, executionTime);

            return result;
        } catch (Exception exception) {
            // Calculate execution time even if method throws exception
            long executionTime = System.currentTimeMillis() - startTime;

            // Log execution time with error
            LOGGER.error("Method {} failed after {} ms: {}",
                    metricName, executionTime, exception.getMessage());

            throw exception;
        } catch (Error error) {
            // Log execution time with error
            long executionTime = System.currentTimeMillis() - startTime;
            LOGGER.error("Method {} failed with Error after {} ms: {}",
                    metricName, executionTime, error.getMessage());

            // Re-throw errors directly without wrapping
            throw error;
        }
    }
}
