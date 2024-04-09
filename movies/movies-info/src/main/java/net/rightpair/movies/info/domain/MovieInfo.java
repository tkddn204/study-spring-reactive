package net.rightpair.movies.info.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document
public class MovieInfo {
    @Id
    private String movieInfoId;

    @NotBlank(message = "movieInfo.name의 내용이 비어있습니다.")
    private String name;

    @NotNull
    @Positive(message = "movieInfo.year의 값은 항상 양수여야 합니다.")
    private Integer year;

    private List<@NotBlank(message = "movieInfo.cast의 내용이 비어있습니다.") String> cast;

    private LocalDate releasedAt;

    public void addActor(String newActor) {
        cast.add(newActor);
    }

    public void update(MovieInfo updatedMovieInfo) {
        this.name = Objects.isNull(updatedMovieInfo.getName()) ? this.name : updatedMovieInfo.getName();
        this.year = Objects.isNull(updatedMovieInfo.getYear()) ? this.year : updatedMovieInfo.getYear();
        this.cast = Objects.isNull(updatedMovieInfo.getCast()) ? this.cast : updatedMovieInfo.getCast();
        this.releasedAt = Objects.isNull(updatedMovieInfo.getReleasedAt()) ? this.releasedAt : updatedMovieInfo.getReleasedAt();
    }
}
