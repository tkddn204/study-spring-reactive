package net.rightpair.studyspring.movieinfo.controller;

import lombok.RequiredArgsConstructor;
import net.rightpair.studyspring.movieinfo.domain.MovieInfo;
import net.rightpair.studyspring.movieinfo.service.MovieInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/movieinfos")
public class MovieInfoController {

    private final MovieInfoService movieInfoService;

    @PostMapping
    public ResponseEntity<Mono<MovieInfo>> addMovieInfo(
            @RequestBody MovieInfo movieInfo
    ) {
        return ResponseEntity.created(URI.create("/"))
                .body(movieInfoService.addMovieInfo(movieInfo));
    }

    @GetMapping
    public ResponseEntity<Flux<MovieInfo>> getAllMovieInfos()  {
        return ResponseEntity.ok(movieInfoService.getAllMovieInfos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<MovieInfo>> getMovieInfoById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(movieInfoService.getMovieInfoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<MovieInfo>> updateMovieInfo(
            @RequestBody MovieInfo updatedMovieInfo,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(movieInfoService.updateMovieInfo(updatedMovieInfo, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> deleteMovieInfo(
            @PathVariable String id
    ) {
        movieInfoService.deleteMovieInfo(id);
        return ResponseEntity.noContent().build();
    }
}
