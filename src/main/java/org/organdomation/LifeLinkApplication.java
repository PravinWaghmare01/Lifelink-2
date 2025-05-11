package org.organdomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.organdomation.model")
@EnableJpaRepositories("org.organdomation.repository")
public class LifeLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeLinkApplication.class, args);
    }
}