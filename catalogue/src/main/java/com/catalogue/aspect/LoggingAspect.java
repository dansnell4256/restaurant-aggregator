package com.catalogue.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method executions.
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * Pointcut for all service methods.
     */
    @Pointcut("execution(* com.catalogue.service.*.*(..))")
    public void servicePointcut() { }

    /**
     * Pointcut for all controller methods.
     */
    @Pointcut("execution(* com.catalogue.controller.*.*(..))")
    public void controllerPointcut() { }

    /**
     * Advice to log method entry, exit, and execution time.
     *
     * @param joinPoint Join point for advice
     * @return The result of proceeding with the intercepted method call
     * @throws Throwable If proceeding with the intercepted method call throws
     */
    @Around("servicePointcut() || controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Log method entry
        logger.debug("Entering: {}.{}()", className, methodName);

        // Record start time
        long startTime = System.currentTimeMillis();

        try {
            // Execute the method
            Object result = joinPoint.proceed();

            // Record end time and calculate duration
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Log method exit with execution time
            logger.debug("Exiting: {}.{}() with result: {}. Time taken: {} ms",
                    className, methodName, result, duration);

            return result;
        } catch (Exception e) {
            // Log exceptions
            logger.error("Exception in {}.{}() with cause: {}",
                    className, methodName, e.getMessage());
            throw e;
        }
    }
}
