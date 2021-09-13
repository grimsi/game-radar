package de.grimsi.gameradar.backend.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Profile("dev")
@Component
@Slf4j
public class LatencyInducingRequestInterceptor implements HandlerInterceptor {

    private final Duration RESPONSE_DELAY = Duration.ofSeconds(2);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws InterruptedException {
        log.debug("Delaying response for request from '{}' by {} seconds.", request.getRequestURI(), RESPONSE_DELAY.getSeconds());
        Thread.sleep(RESPONSE_DELAY.toMillis());
    }
}
