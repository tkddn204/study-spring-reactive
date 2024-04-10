package net.rightpair.movies.client;

import lombok.RequiredArgsConstructor;
import net.rightpair.movies.annotation.RestClient;
import net.rightpair.movies.domain.Review;
import net.rightpair.movies.exception.MoviesReviewClientException;
import net.rightpair.movies.exception.MoviesReviewServerException;
import net.rightpair.movies.util.RetryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestClient
@RequiredArgsConstructor
public class MoviesReviewRestClient {
    private final WebClient webClient;

    @Value("${modules.movies-review.url}")
    private String moviesReviewUrl;

    public Flux<Review> retrieveMoviesReview(String movieId) {
        var url = moviesReviewUrl.concat("/v1/reviews/movies/{id}");

        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage ->
                                    Mono.error(new MoviesReviewClientException(responseMessage))
                            );
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseMessage ->
                                        Mono.error(new MoviesReviewServerException(
                                                "영화 리뷰 정보를 찾는 데 서버 에러가 발생했습니다 : " + responseMessage
                                        ))
                                ))
                .bodyToFlux(Review.class)
                .retryWhen(RetryUtil.retry3BackOffDelayFixedOneSecond());
    }
}
