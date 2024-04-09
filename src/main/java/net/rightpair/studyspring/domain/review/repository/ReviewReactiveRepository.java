package net.rightpair.studyspring.domain.review.repository;

import net.rightpair.studyspring.domain.review.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findAllByMovieInfoId(Long movieInfoId);

}
