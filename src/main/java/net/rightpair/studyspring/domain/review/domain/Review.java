package net.rightpair.studyspring.domain.review.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document
public class Review {
    @Id
    private String reviewId;

    @NotNull(message = "rating.movieInfoId : movieInfoId을 입력해야 합니다.")
    private Long movieInfoId;
    private String comment;

    @Min(value = 0L, message = "rating.negative : rating은 0 이상이어야 합니다.")
    private Double rating;

    public Review update(String comment, Double rating) {
        this.comment = Objects.isNull(comment) ? this.comment : comment;
        this.rating = Objects.isNull(rating) ? this.rating : rating;
        return this;
    }
}
