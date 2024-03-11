package org.example.vkintership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VkInternshipApplication {
    public static void main(String[] args) {
        SpringApplication.run(VkInternshipApplication.class, args);
    }
}