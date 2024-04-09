package net.rightpair.movies.info.controller;

import net.rightpair.movies.domain.MovieInfo;
import net.rightpair.movies.info.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MovieInfoControllerIntegrationTest {

    private static final String MOVIES_INFO_URL = "/v1/movieinfos";
    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(new MovieInfo(
                null, "movie name 1", 2005, List.of("Actor1", "Actor2"), LocalDate.parse("2020-01-23")
        ), new MovieInfo(
                null, "movie name 2", 2008, List.of("Actor1", "Actor3"), LocalDate.parse("2015-02-12")
        ), new MovieInfo(
                "abc", "movie name 3", 2010, List.of("Actor1", "Actor4"), LocalDate.parse("2017-07-05")
        ));
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        //given
        var movieInfo = new MovieInfo(
                null, "new movie", 2005, List.of("Actor2", "Actor3"), LocalDate.parse("2007-11-22")
        );

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

        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("movie name 3");
//                .consumeWith(result -> {
//                    var storedMovieInfo = result.getResponseBody();
//                    Assertions.assertNotNull(storedMovieInfo);
//                    Assertions.assertNotNull(storedMovieInfo.getMovieInfoId());
//                });

        //then

    }

    @Test
    void fail_getMovieInfoById() {
        //given
        var movieInfoId = "nothing";

        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();

        //then

    }

    @Test
    void updateMovieInfo() {
        //given
        var movieInfoId = "abc";
        var updatedMovieInfo = new MovieInfo(
                null, "updated movie", 2020, List.of("Actor1", "Actor5"), LocalDate.parse("2022-05-12")
        );

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
                .isNoContent()
                .expectBody()
                .consumeWith(result -> movieInfoRepository.findById(movieInfoId)
                        .subscribe(
                                movieInfo -> Assertions.fail(movieInfo.toString()),
                                Assertions::fail
                        ));
        //then
    }
}