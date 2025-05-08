package com.common.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@Profile("!local") // Only active when local profile is NOT active
public class DatabaseConfig {

    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");

        // If environment variables are not set, use the properties
        if (username == null) {
            username = env.getProperty("spring.datasource.username");
        }
        if (password == null) {
            password = env.getProperty("spring.datasource.password");
        }

        return DataSourceBuilder.create()
                .url(env.getProperty("spring.datasource.url"))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name",
                        env.getProperty("spring.datasource.driverClassName")))
                .username(username)
                .password(password)
                .build();
    }
}
