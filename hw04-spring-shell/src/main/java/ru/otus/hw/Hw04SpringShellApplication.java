package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.otus.hw.config.AppProperties;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class Hw04SpringShellApplication {
    public static void main(String[] args) {
        SpringApplication.run(Hw04SpringShellApplication.class, args);
    }
}