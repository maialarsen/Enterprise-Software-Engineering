package springboot.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"springboot.services"})
@EntityScan("springboot.model")
@EnableJpaRepositories("springboot.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(springboot.main.Application.class, args);
    }
}
