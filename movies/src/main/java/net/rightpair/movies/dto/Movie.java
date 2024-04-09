package net.rightpair.movies.dto;

import net.rightpair.movies.domain.MovieInfo;
import net.rightpair.movies.domain.Review;

import java.util.List;

public record Movie(
        MovieInfo movieInfo,
        List<Review> reviewList
) {
}
