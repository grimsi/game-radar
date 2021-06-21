package de.grimsi.gameradar.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

// GSON will indirectly get loaded from the minecraft-server-status-plugin, so we have to manually disable the auto-configuration
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@EnableConfigurationProperties
@EnableScheduling
public class GameRadar {
    public static void main(String[] args) {
        SpringApplication.run(GameRadar.class);
    }
}
