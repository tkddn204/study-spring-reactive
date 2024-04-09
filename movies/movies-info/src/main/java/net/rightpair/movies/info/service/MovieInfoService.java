package net.rightpair.movies.info.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rightpair.movies.domain.MovieInfo;
import net.rightpair.movies.info.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo).log();
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.update(updatedMovieInfo);
                    return movieInfoRepository.save(movieInfo);
                });
    }

    public void deleteMovieInfo(String id) {
        movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> getAllMovieInfosByYear(Integer year) {
        return movieInfoRepository.findAllByYear(year);
    }
}
