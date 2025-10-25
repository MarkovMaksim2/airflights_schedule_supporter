package com.airflights.restrictedzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.airflights.restrictedZone.feign")
public class RestrictedZoneServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestrictedZoneServiceApplication.class, args);
    }
}