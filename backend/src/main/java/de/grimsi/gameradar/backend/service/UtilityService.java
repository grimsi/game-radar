package de.grimsi.gameradar.backend.service;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UtilityService {
    public String prettyFormatDuration(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    public String getHostFromURI(String uri) {
        return uri.substring(0, uri.indexOf('/', 7));
    }
}
