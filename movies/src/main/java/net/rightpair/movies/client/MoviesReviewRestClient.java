package net.rightpair.movies.client;

import lombok.RequiredArgsConstructor;
import net.rightpair.movies.annotation.RestClient;
import net.rightpair.movies.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
                .bodyToFlux(Review.class);
    }
}
