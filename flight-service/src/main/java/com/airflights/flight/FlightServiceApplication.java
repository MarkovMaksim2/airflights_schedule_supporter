package com.airflights.flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.airflights.flight.feign")
public class FlightServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightServiceApplication.class, args);
    }
}