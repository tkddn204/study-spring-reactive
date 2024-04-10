package net.rightpair.movies.util;

import net.rightpair.movies.exception.ExternalServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

public class RetryUtil {
    public static RetryBackoffSpec retry3BackOffDelayFixedOneSecond() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex -> ex instanceof ExternalServerException)
                .onRetryExhaustedThrow(
                        ((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()))
                );
    }
}
