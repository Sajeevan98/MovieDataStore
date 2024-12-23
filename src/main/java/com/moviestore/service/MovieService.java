package com.moviestore.service;

import com.moviestore.dto.MovieDto;
import com.moviestore.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface MovieService {

    // methods for CRUD functionalities...
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;
    MovieDto getMovie(Integer movieId);
    List<MovieDto> getAllMovies();
    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;
    String deleteMovie(Integer movieId) throws IOException;

    // methods for Pagination & Sorting...
    MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pageSize);
    MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir);

}
