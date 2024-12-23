package com.moviestore.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private Integer movieId;

    @NotBlank(message = "Title field cannot be empty!")
    private String title;

    @NotBlank(message = "Director field cannot be empty!")
    private String director;

    @NotBlank(message = "Studio field cannot be empty!")
    private String studio;

    @NotBlank(message = "Music-Director field cannot be empty!")
    private String musicDirector;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Poster field cannot be empty!")
    private String poster;

    // this is a new attribute in Dto, for making the url.
    @NotBlank(message = "Poster-Url field cannot be empty!")
    private String posterUrl;

}
