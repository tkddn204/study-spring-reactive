package net.rightpair.studyspring.repository;

import net.rightpair.studyspring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@DataMongoTest
class MovieInfoRepositoryTest {
    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(new MovieInfo(
                null, "movie name 1", 2005, List.of("Actor1", "Actor2"), LocalDate.parse("2020-01-23")
        ), new MovieInfo(
                null, "movie name 2", 2005, List.of("Actor1", "Actor3"), LocalDate.parse("2015-02-12")
        ), new MovieInfo(
                "abc", "movie name 3", 2005, List.of("Actor1", "Actor4"), LocalDate.parse("2017-07-05")
        ));
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        //given

        //when
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        //given
        String id = "abc";

        //when
        var moviesInfoMono = movieInfoRepository.findById(id).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> Assertions.assertEquals("movie name 3", movieInfo.getName()))
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        //given
        var expected = new MovieInfo(
                null, "movie name 4", 2005, List.of("Actor1", "Actor2"), LocalDate.parse("2010-01-23")
        );

        //when
        var moviesInfoMono = movieInfoRepository.save(expected).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> {
                    Assertions.assertNotNull(movieInfo.getMovieInfoId());
                    Assertions.assertEquals("movie name 4", movieInfo.getName());
                }).verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        //given
        var storedMovieInfoMono = movieInfoRepository.findById("abc");
        var storedMovieInfo = storedMovieInfoMono.block();

        //when
        Objects.requireNonNull(storedMovieInfo).addActor("New Actor");
        movieInfoRepository.save(storedMovieInfo).block();

        //then
        StepVerifier.create(storedMovieInfoMono)
                .assertNext(movieInfo -> Assertions.assertEquals(3, movieInfo.getCast().size()))
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        //given
        String id = "abc";

        //when
        movieInfoRepository.deleteById(id).block();
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2).verifyComplete();
    }
}