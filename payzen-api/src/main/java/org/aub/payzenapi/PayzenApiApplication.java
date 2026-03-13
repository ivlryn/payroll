package org.aub.payzenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "org.aub.payzenapi",
        "org.aub.payzenapi.model.mapper"
})
@EntityScan(basePackages = "org.aub.payzenapi.model")
@EnableJpaRepositories(basePackages = "org.aub.payzenapi.repository")

// Ensures services, filters, etc. are picked up
public class PayzenApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayzenApiApplication.class, args);
    }

}
