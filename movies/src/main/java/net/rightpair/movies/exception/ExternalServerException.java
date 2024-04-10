package net.rightpair.movies.exception;

public class ExternalServerException extends RuntimeException {
    public ExternalServerException(String message) {
        super(message);
    }
}
