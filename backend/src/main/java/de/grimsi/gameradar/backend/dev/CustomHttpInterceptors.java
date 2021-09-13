package de.grimsi.gameradar.backend.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("dev")
@Configuration
public class CustomHttpInterceptors implements WebMvcConfigurer {
    
    @Autowired
    private LatencyInducingRequestInterceptor latencyInducingRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(latencyInducingRequestInterceptor);
    }
}
