package net.rightpair.studyspring.domain.review.router;

import net.rightpair.studyspring.domain.review.domain.Review;
import net.rightpair.studyspring.domain.review.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReviewRouterTest {

    private final static String REVIEWS_URL = "/v1/reviews";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {
        var reviewList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Good Movie", 7.0),
                new Review(null, 2L, "Not Bad Movie", 5.0)
        );
        reviewReactiveRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Nice Movie", 8.0);

        //when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(result -> {
                    var savedReview = result.getResponseBody();
                    Assertions.assertNotNull(savedReview);
                    Assertions.assertNotNull(savedReview.getReviewId());
                });

        //then
    }

}