//package com.catalogue.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import io.micrometer.core.aop.TimedAspect;
//import io.micrometer.core.instrument.MeterRegistry;
//
///**
// * Configuration for application metrics using Micrometer.
// */
//@Configuration
//public class MetricsConfig {
//
//    /**
//     * Creates a TimedAspect bean to support the @Timed annotation.
//     *
//     * @param registry The meter registry
//     * @return TimedAspect bean
//     */
//    @Bean
//    public TimedAspect timedAspect(MeterRegistry registry) {
//        return new TimedAspect(registry);
//    }
//}
