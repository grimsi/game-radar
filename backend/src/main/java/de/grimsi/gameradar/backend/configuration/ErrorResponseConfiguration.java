package de.grimsi.gameradar.backend.configuration;

import de.grimsi.gameradar.backend.error.ApiError;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorResponseConfiguration extends ResponseEntityExceptionHandler {

    @Value("${spring.datasource.db-name}")
    private String dbName;

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ObjectError> fieldErrors = ex.getBindingResult().getAllErrors();

        List<String> errors = fieldErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());

        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

        requestUri = Encode.forHtml(requestUri);

        ApiError apiError = new ApiError(status, errors, requestUri);

        return new ResponseEntity<>(apiError, headers, status);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

        requestUri = Encode.forHtml(requestUri);

        String error = getShortExceptionMessage(ex);

        ApiError apiError = new ApiError(status, Collections.singletonList(error), requestUri);

        return new ResponseEntity<>(apiError, headers, status);
    }

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

        requestUri = Encode.forHtml(requestUri);

        ApiError apiError = new ApiError(ex.getStatus(), Collections.singletonList(ex.getReason()), requestUri);

        return new ResponseEntity<>(apiError, ex.getStatus());
    }

    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

        requestUri = Encode.forHtml(requestUri);

        String entityName = getEntityNameFromDuplicateKeyException(ex);
        String fieldName = getFieldNameFromDuplicateKeyException(ex);
        String error = entityName + "." + fieldName + ".not-unique";

        ApiError apiError = new ApiError(HttpStatus.CONFLICT, Collections.singletonList(error), requestUri);

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

        requestUri = Encode.forHtml(requestUri);

        final String error = "error.insufficient-permissions";

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, Collections.singletonList(error), requestUri);

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();

        requestUri = Encode.forHtml(requestUri);

        String error = getShortExceptionMessage(ex);

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonList(error), requestUri);

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getShortExceptionMessage(Exception ex) {
        int endIndex = ex.getMessage().indexOf("; nested exception");
        if (endIndex > 0) {
            return Objects.requireNonNull(ex.getMessage()).substring(0, endIndex);
        }
        return ex.getMessage();
    }

    private String getEntityNameFromDuplicateKeyException(DuplicateKeyException e) {
        return Pattern.compile("(?<=" + dbName + ".)(.*?)(?= )")
                .matcher(Objects.requireNonNull(e.getMessage()))
                .results()
                .map(MatchResult::group)
                .findFirst()
                .orElse("unknown-entity");
    }

    private String getFieldNameFromDuplicateKeyException(DuplicateKeyException e) {
        return Pattern.compile("(?<=index: )(.*?)(?= dup key:)")
                .matcher(Objects.requireNonNull(e.getMessage()))
                .results()
                .map(MatchResult::group)
                .findFirst()
                .orElse("unknown-property");
    }
}
