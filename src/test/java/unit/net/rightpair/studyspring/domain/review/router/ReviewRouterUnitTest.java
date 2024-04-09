package net.rightpair.studyspring.domain.review.router;

import net.rightpair.studyspring.domain.review.domain.Review;
import net.rightpair.studyspring.domain.review.handler.ReviewHandler;
import net.rightpair.studyspring.domain.review.repository.ReviewReactiveRepository;
import net.rightpair.studyspring.global.GlobalReactiveErrorHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalReactiveErrorHandler.class})
@AutoConfigureWebTestClient
class ReviewRouterUnitTest {

    private final static String REVIEWS_URL = "/v1/reviews";

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Nice Movie", 8.0);
        var expectedReview = new Review("abc", 1L, "Nice Movie", 8.0);
        given(reviewReactiveRepository.save(any(Review.class)))
                .willReturn(Mono.just(expectedReview));

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

    @Test
    void validation_fail_addReview() {
        //given
        var review = new Review(null, null, "Nice Movie", -1.0);
//        var expectedReview = new Review("abc", 1L, "Nice Movie", 8.0);
//        given(reviewReactiveRepository.save(any(Review.class)))
//                .willReturn(Mono.just(expectedReview));

        //when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest();

        //then
    }
}