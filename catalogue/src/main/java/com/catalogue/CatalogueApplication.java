package com.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.catalogue", "com.common"})
@EntityScan({"com.catalogue.model", "com.common.model"})
public class CatalogueApplication {

    public CatalogueApplication() {

    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogueApplication.class, args);
    }
}
