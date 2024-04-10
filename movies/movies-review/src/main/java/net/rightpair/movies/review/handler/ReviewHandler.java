package net.rightpair.movies.review.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rightpair.movies.domain.Review;
import net.rightpair.movies.review.annotation.Handler;
import net.rightpair.movies.review.exception.ReviewDataException;
import net.rightpair.movies.review.exception.ReviewNotFoundException;
import net.rightpair.movies.review.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.stream.Collectors;

@Slf4j
@Handler
@RequiredArgsConstructor
public class ReviewHandler {

    private final Validator validator;

    private final ReviewReactiveRepository reviewReactiveRepository;

    private final Sinks.Many<Review> moviesReviewSink = Sinks.many().replay().latest();

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validateReview)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(moviesReviewSink::tryEmitNext)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validateReview(Review review) {
        var constraintViolations = validator.validateObject(review);
        log.error("constraintViolations: {}", constraintViolations);
        if (constraintViolations.hasErrors()) {
            var errorMessage = constraintViolations.getAllErrors()
                    .stream()
                    .map(ObjectError::toString)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }

    }

    public Mono<ServerResponse> getReview(ServerRequest request) {
        var reviewId = request.queryParam("id");
        return reviewId.map(s -> request.bodyToMono(Review.class)
                        .flatMap(review -> reviewReactiveRepository.findById(s))
                        .flatMap(ServerResponse.ok()::bodyValue))
                .orElseGet(() -> ServerResponse.badRequest().bodyValue("review Id를 입력해주세요."));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            var reviewFlux = reviewReactiveRepository.findAllByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewsResponse(reviewFlux);
        }

        var reviewFlux = reviewReactiveRepository.findAll();
        return buildReviewsResponse(reviewFlux);
    }

    private static Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviewFlux) {
        return ServerResponse.ok().body(reviewFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(req -> req.update(req.getComment(), req.getRating()))
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(ServerResponse.ok()::bodyValue)
                )
                .switchIfEmpty(Mono.error(new ReviewNotFoundException(
                        "해당 ID의 Review를 찾을 수 없습니다 : %s".formatted(reviewId))));
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewReactiveRepository.deleteById(reviewId))
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(moviesReviewSink.asFlux(), Review.class)
                .log();
    }
}
