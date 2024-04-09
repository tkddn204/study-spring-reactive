package net.rightpair.studyspring.domain.movieinfo.repository;

import net.rightpair.studyspring.domain.movieinfo.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

    Flux<MovieInfo> findAllByYear(int year);

}
