package com.moviestore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Title field cannot be empty!")
    private String title;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "Director field cannot be empty!")
    private String director;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Studio field cannot be empty!")
    private String studio;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "Music-Director field cannot be empty!")
    private String musicDirector;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Poster field cannot be empty!")
    private String poster;

}
