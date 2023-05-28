package tech.island4george;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AliyunDdnsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AliyunDdnsApplication.class, args);
    }
}
