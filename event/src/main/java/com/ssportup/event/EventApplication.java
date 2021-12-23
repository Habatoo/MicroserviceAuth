package com.ssportup.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Точка входа в модуль event.
 *
 * @author habatoo
 * @version 0.001
 */
@SpringBootApplication
@EnableEurekaClient
public class EventApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }
}
