package com.nutrition;

import com.nutrition.service.INutritionService;
import com.nutrition.service.NutritionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetAddress;

/**
 * Spring Boot Application Entry Point for NutriTrack Web & Mobile REST API.
 * Bridges to existing Core Java Nutrition Services and Models.
 */
@SpringBootApplication
public class NutriTrackWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutriTrackWebApplication.class, args);
        printStartupBanner();
    }

    /**
     * Singleton Bean providing existing core business logic and persistent storage.
     */
    @Bean
    public INutritionService nutritionService() {
        return new NutritionService();
    }

    /**
     * Global CORS configuration to allow local web and mobile clients on the Wi-Fi network.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    private static void printStartupBanner() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("==================================================================");
            System.out.println("  🥗 NutriTrack Spring Boot Backend Ready!");
            System.out.println("  Local API:   http://localhost:8080/api");
            System.out.println("  Network API: http://" + ip + ":8080/api");
            System.out.println("  Mobile/Web Frontend will connect seamlessly.");
            System.out.println("==================================================================");
        } catch (Exception ignored) {
            System.out.println(">> NutriTrack Backend running on http://localhost:8080/api");
        }
    }
}
