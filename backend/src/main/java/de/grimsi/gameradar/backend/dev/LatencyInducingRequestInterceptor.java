package de.grimsi.gameradar.backend.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Profile("latency")
@Component
@Slf4j
public class LatencyInducingRequestInterceptor implements HandlerInterceptor {

    private final Duration RESPONSE_DELAY = Duration.ofSeconds(2);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws InterruptedException {
        log.debug("Delaying response for request from '{}' by {} seconds.", request.getRequestURI(), RESPONSE_DELAY.getSeconds());
        Thread.sleep(RESPONSE_DELAY.toMillis());
        return true;
    }
}
