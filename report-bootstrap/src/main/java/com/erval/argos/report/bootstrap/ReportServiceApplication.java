package com.erval.argos.report.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Entry point for the report service Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = "com.erval.argos")
@EnableMongoRepositories(basePackages = "com.erval.argos.report.adapters.mongo.repositories")
public class ReportServiceApplication {
    /**
     * Boots the Spring application.
     *
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}
