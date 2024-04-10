package net.rightpair.movies.client;

import lombok.RequiredArgsConstructor;
import net.rightpair.movies.annotation.RestClient;
import net.rightpair.movies.domain.MovieInfo;
import net.rightpair.movies.exception.MoviesInfoClientException;
import net.rightpair.movies.exception.MoviesInfoServerException;
import net.rightpair.movies.util.RetryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                                "해당 영화의 정보를 찾을 수 없습니다 : " + movieId
                        ));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage ->
                                    Mono.error(new MoviesInfoClientException(responseMessage))
                            );
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseMessage ->
                                        Mono.error(new MoviesInfoServerException(
                                                "영화 정보를 찾는 데 서버 에러가 발생했습니다 : " + responseMessage
                                        ))
                                ))
                .bodyToMono(MovieInfo.class)
//                .retry(3)
                .retryWhen(RetryUtil.retry3BackOffDelayFixedOneSecond())
                .log();
    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        var url = moviesInfoUrl.concat("/v1/movieinfos/stream");
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(responseMessage ->
                                        Mono.error(new MoviesInfoClientException(responseMessage))
                                ))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(responseMessage ->
                                        Mono.error(new MoviesInfoServerException(
                                                "영화 정보를 찾는 데 서버 에러가 발생했습니다 : " + responseMessage
                                        ))
                                ))
                .bodyToFlux(MovieInfo.class)
//                .retry(3)
                .retryWhen(RetryUtil.retry3BackOffDelayFixedOneSecond())
                .log();
    }
}
