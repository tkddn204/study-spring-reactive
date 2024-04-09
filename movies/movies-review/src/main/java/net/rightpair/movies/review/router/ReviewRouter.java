package net.rightpair.movies.review.router;

import net.rightpair.movies.review.handler.ReviewHandler;
import net.rightpair.movies.review.annotation.Router;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Router
public class ReviewRouter {
    @Bean
    public RouterFunction<ServerResponse> reviewRoute(ReviewHandler reviewHandler) {
        return route()
                .nest(path("/v1/reviews"), builder -> builder
                        .POST("", reviewHandler::addReview)
                        .GET("/{id}", reviewHandler::getReview)
                        .GET("", reviewHandler::getReviews)
                        .GET("/movies/{movieInfoId}", reviewHandler::getReviews)
                        .PUT("/{id}", reviewHandler::updateReview)
                        .DELETE("/{id}", reviewHandler::deleteReview)
                )
                .GET("/v1/hello", request -> ServerResponse.ok().bodyValue("hello, router!"))
                .build();
    }
}
