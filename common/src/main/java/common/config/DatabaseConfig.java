//package com.common.config;
//
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DatabaseConfig {
//
//    private final Environment env;
//
//    public DatabaseConfig(Environment env) {
//        this.env = env;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .url(env.getProperty("spring.datasource.url"))
//                .driverClassName(env.getProperty("spring.datasource.driverClassName"))
//                .username(env.getProperty("SPRING_DATASOURCE_USERNAME"))
//                .password(env.getProperty("SPRING_DATASOURCE_PASSWORD"))
//                .build();
//    }
//}
