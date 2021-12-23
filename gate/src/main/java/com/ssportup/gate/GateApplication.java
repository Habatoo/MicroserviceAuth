package com.ssportup.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Точка входа в модуль gate.
 *
 * @author habatoo
 * @version 0.001
 */
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class GateApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateApplication.class, args);
    }
}
