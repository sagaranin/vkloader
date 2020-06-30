package ru.larnerweb.vkloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApiContextInitializer.init();  // tg bot
        SpringApplication.run(Application.class, args);
    }
}
