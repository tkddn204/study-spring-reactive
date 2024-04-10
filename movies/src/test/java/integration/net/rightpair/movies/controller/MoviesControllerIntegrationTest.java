package net.rightpair.movies.controller;

import net.rightpair.movies.dto.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {
                "modules.movies-info.url=http://localhost:8084",
                "modules.movies-review.url=http://localhost:8084"
        }
)
class MoviesControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void retrieveMoviesById() {
        //given
        var movieId = "abc";
        stubFor(get(urlPathEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviesInfoResponse.json")
                )
        );
        stubFor(get(urlPathEqualTo("/v1/reviews/movies/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviesReviewResponse.json")
                )
        );

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(result -> {
                    var movie = result.getResponseBody();
                    Assertions.assertNotNull(movie);
                    Assertions.assertEquals(2, movie.reviewList().size());
                    Assertions.assertEquals("Batman Begins", movie.movieInfo().getName());
                });

        //then
    }

    @Test
    void fail_info_404_retrieveMoviesById() {
        //given
        var movieId = "abc";
        stubFor(get(urlPathEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(404)
                )
        );
        stubFor(get(urlPathEqualTo("/v1/reviews/movies/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviesReviewResponse.json")
                )
        );

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("해당 영화의 정보를 찾을 수 없습니다 : " + movieId);

        //then
    }

    @Test
    void fail_review_404_retrieveMoviesById() {
        //given
        var movieId = "abc";
        stubFor(get(urlPathEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviesInfoResponse.json")
                )
        );
        stubFor(get(urlPathEqualTo("/v1/reviews/movies/" + movieId))
                .willReturn(aResponse()
                        .withStatus(404)
                )
        );

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(result -> {
                    var movie = result.getResponseBody();
                    Assertions.assertNotNull(movie);
                    Assertions.assertEquals(0, movie.reviewList().size());
                    Assertions.assertEquals("Batman Begins", movie.movieInfo().getName());
                });

        //then
    }

    @Test
    void fail_info_5XX_retrieveMoviesById() {
        //given
        var movieId = "abc";
        stubFor(get(urlPathEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("MoviesInfo Service Unavailable")
                )
        );
        stubFor(get(urlPathEqualTo("/v1/reviews/movies/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviesReviewResponse.json")
                )
        );

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        //then
//        WireMock.verify(4, getRequestedFor(urlPathEqualTo("/v1/movieinfos/" + movieId)));
    }
}