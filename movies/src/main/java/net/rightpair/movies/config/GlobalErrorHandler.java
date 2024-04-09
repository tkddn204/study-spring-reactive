package net.rightpair.movies.config;

import lombok.extern.slf4j.Slf4j;
import net.rightpair.movies.exception.MoviesInfoClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleClientException(MoviesInfoClientException exception) {
        log.error("MoviesInfo Client Error : ", exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        log.error("RuntimeException Error : {}", exception.getMessage());
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }
}
