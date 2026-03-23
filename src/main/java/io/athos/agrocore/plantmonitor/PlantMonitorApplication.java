package io.athos.agrocore.plantmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(

)

public class PlantMonitorApplication {

    static void main(String[] args) {
        SpringApplication.run(PlantMonitorApplication.class, args);
    }

}
