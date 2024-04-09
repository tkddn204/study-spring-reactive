package net.rightpair.movies.controller;

import lombok.RequiredArgsConstructor;
import net.rightpair.movies.client.MoviesInfoRestClient;
import net.rightpair.movies.client.MoviesReviewRestClient;
import net.rightpair.movies.dto.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final MoviesReviewRestClient moviesReviewRestClient;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> moviesReviewRestClient.retrieveMoviesReview(movieId)
                        .collectList()
                        .map(reviewList -> new Movie(movieInfo, reviewList))
                );
    }
}
