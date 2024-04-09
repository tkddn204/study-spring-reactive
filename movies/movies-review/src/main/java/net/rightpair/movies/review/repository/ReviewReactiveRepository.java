package net.rightpair.movies.review.repository;

import net.rightpair.movies.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findAllByMovieInfoId(Long movieInfoId);

}
