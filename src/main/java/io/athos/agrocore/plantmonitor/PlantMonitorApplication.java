package io.athos.agrocore.plantmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(
//        exclude = {
//        SecurityAutoConfiguration.class,
//        UserDetailsServiceAutoConfiguration.class}
)

public class PlantMonitorApplication {

    static void main(String[] args) {
        SpringApplication.run(PlantMonitorApplication.class, args);
    }

}
