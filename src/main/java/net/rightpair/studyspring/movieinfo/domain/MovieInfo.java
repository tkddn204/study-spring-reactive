package net.rightpair.studyspring.movieinfo.domain;

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
    private String name;
    private Integer year;
    private List<String> cast;
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
