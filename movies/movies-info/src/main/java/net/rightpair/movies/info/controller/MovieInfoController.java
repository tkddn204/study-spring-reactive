package net.rightpair.movies.info.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rightpair.movies.info.domain.MovieInfo;
import net.rightpair.movies.info.service.MovieInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/movieinfos")
public class MovieInfoController {

    private final MovieInfoService movieInfoService;

    @PostMapping
    public Mono<ResponseEntity<MovieInfo>> addMovieInfo(
            @RequestBody @Valid MovieInfo movieInfo
    ) {
        return movieInfoService.addMovieInfo(movieInfo)
                .map(ResponseEntity.created(URI.create("/"))::body);
    }

    @GetMapping
    public Mono<ResponseEntity<List<MovieInfo>>> getAllMovieInfos(
            @RequestParam(value = "year", required = false) Integer year
    ) {
        if (year != null) {
            return movieInfoService.getAllMovieInfosByYear(year)
                    .collectList()
                    .map(ResponseEntity.ok()::body)
                    .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
        }

        return movieInfoService.getAllMovieInfos()
                .collectList()
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(
            @PathVariable String id
    ) {
        return movieInfoService.getMovieInfoById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(
            @RequestBody MovieInfo updatedMovieInfo,
            @PathVariable String id
    ) {
        return movieInfoService.updateMovieInfo(updatedMovieInfo, id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieInfo(
            @PathVariable String id
    ) {
        movieInfoService.deleteMovieInfo(id);
        return ResponseEntity.noContent().build();
    }
}
