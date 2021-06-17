package de.grimsi.gameradar.backend.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ApiError {

    private final ZonedDateTime timestamp = ZonedDateTime.now();

    private final String error;

    private final int status;

    private final Object errors;

    private final String path;

    public ApiError(HttpStatus status, Object errors, String path) {
        this.status = status.value();
        error = status.getReasonPhrase();
        this.errors = errors;
        this.path = path;
    }

}
