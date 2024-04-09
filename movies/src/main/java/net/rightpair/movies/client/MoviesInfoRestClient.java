package net.rightpair.movies.client;

import lombok.RequiredArgsConstructor;
import net.rightpair.movies.annotation.RestClient;
import net.rightpair.movies.domain.MovieInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestClient
@RequiredArgsConstructor
public class MoviesInfoRestClient {
    private final WebClient webClient;

    @Value("${modules.movies-info.url}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var url = moviesInfoUrl.concat("/v1/movieinfos/{id}");
        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
