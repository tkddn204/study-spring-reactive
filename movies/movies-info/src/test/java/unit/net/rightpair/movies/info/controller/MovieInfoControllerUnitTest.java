package net.rightpair.movies.info.controller;

import net.rightpair.movies.domain.MovieInfo;
import net.rightpair.movies.info.service.MovieInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {
    private static final String MOVIES_INFO_URL = "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;

    @Test
    void addMovieInfo() {
        //given
        var movieInfo = new MovieInfo(
                "id", "new movie", 2005, List.of("Actor2", "Actor3"), LocalDate.parse("2007-11-22")
        );
        given(movieInfoService.addMovieInfo(any(MovieInfo.class)))
                .willReturn(Mono.just(movieInfo));

        //when
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(result -> {
                    var storedMovieInfo = result.getResponseBody();
                    Assertions.assertNotNull(storedMovieInfo);
                    Assertions.assertNotNull(storedMovieInfo.getMovieInfoId());
                });

        //then

    }

    @Test
    void getAllMovieInfo() {
        //given
        var movieInfos = List.of(new MovieInfo(
                null, "movie name 1", 2005, List.of("Actor1", "Actor2"), LocalDate.parse("2020-01-23")
        ), new MovieInfo(
                null, "movie name 2", 2005, List.of("Actor1", "Actor3"), LocalDate.parse("2015-02-12")
        ), new MovieInfo(
                "abc", "movie name 3", 2005, List.of("Actor1", "Actor4"), LocalDate.parse("2017-07-05")
        ));

        given(movieInfoService.getAllMovieInfos())
                .willReturn(Flux.fromIterable(movieInfos));

        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3)
                .consumeWith(result -> {
                    var storedMovieInfo = result.getResponseBody();
                    Assertions.assertNotNull(storedMovieInfo);
                    storedMovieInfo.forEach(Assertions::assertNotNull);
                });

        //then
    }

    @Test
    void getAllMovieInfoByYear() {
        //given
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                .queryParam("year", 2005)
                .buildAndExpand().toUri();

        var movieInfos = List.of(new MovieInfo(
                null, "movie name 1", 2005, List.of("Actor1", "Actor2"), LocalDate.parse("2020-01-23")
        ));

        given(movieInfoService.getAllMovieInfosByYear(anyInt()))
                .willReturn(Flux.fromIterable(movieInfos));

        //when
        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1)
                .consumeWith(result -> {
                    var storedMovieInfo = result.getResponseBody();
                    Assertions.assertNotNull(storedMovieInfo);
                    storedMovieInfo.forEach(Assertions::assertNotNull);
                });

        //then
    }

    @Test
    void getMovieInfoById() {
        //given
        var movieInfoId = "abc";
        given(movieInfoService.getMovieInfoById(anyString()))
                .willReturn(
                        Mono.just(
                                new MovieInfo(
                                        movieInfoId,
                                        "movie",
                                        2005,
                                        List.of("Actor1", "Actor4"),
                                        LocalDate.parse("2017-07-05")
                                ))
                );

        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("movie");

        //then

    }

    @Test
    void updateMovieInfo() {
        //given
        var movieInfoId = "abc";
        var updatedMovieInfo = new MovieInfo(
                movieInfoId, "updated movie", 2020, List.of("Actor1", "Actor5"), LocalDate.parse("2022-05-12")
        );
        given(movieInfoService.updateMovieInfo(any(MovieInfo.class), anyString()))
                .willReturn(Mono.just(updatedMovieInfo));

        //when
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(result -> {
                    var storedMovieInfo = result.getResponseBody();
                    Assertions.assertNotNull(storedMovieInfo);
                    Assertions.assertEquals(storedMovieInfo.getName(), updatedMovieInfo.getName());
                    Assertions.assertEquals(storedMovieInfo.getYear(), updatedMovieInfo.getYear());
                    Assertions.assertEquals(storedMovieInfo.getCast(), updatedMovieInfo.getCast());
                    Assertions.assertEquals(storedMovieInfo.getReleasedAt(), updatedMovieInfo.getReleasedAt());
                });
        //then
    }

    @Test
    void deleteMovieInfo() {
        //given
        var movieInfoId = "abc";

        //when
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();

        //then
        verify(movieInfoService).deleteMovieInfo(anyString());
    }
}