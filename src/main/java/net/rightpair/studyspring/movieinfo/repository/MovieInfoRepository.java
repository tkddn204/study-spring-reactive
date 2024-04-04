package net.rightpair.studyspring.movieinfo.repository;

import net.rightpair.studyspring.movieinfo.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
}
