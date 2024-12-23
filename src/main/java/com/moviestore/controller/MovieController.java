package com.moviestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviestore.dto.MovieDto;
import com.moviestore.dto.MoviePageResponse;
import com.moviestore.exception.EmptyFileException;
import com.moviestore.service.MovieService;
import com.moviestore.utils.AppConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDto) throws IOException, EmptyFileException {
        if(file.isEmpty()){
            throw new EmptyFileException("File field cannot be Empty! Please Send a File.");
        }
        MovieDto mDto = convertToMovieDtoObj(movieDto);
        return new ResponseEntity<>(movieService.addMovie(mDto, file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieByIdHandler(@PathVariable Integer movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMovieHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId, @RequestPart MultipartFile file, @RequestPart String movieDto) throws IOException {
        if(file.isEmpty()) file=null;
        MovieDto movieDtoData = convertToMovieDtoObj(movieDto);
        return ResponseEntity.ok(movieService.updateMovie(movieId, movieDtoData, file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    // convert as JSon data...
    private MovieDto convertToMovieDtoObj(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objMapper = new ObjectMapper();
        MovieDto dtoData = objMapper.readValue(movieDtoObj, MovieDto.class);
        return dtoData;
    }

    // Pagination & Sorting
    @GetMapping("allMoviesPage")
    public ResponseEntity<MoviePageResponse> getAllMovieWithPagination(
        @RequestParam (defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
        @RequestParam (defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize
    ){
        return ResponseEntity.ok(movieService.getAllMovieWithPagination(pageNumber, pageSize));
    }

    @GetMapping("allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getAllMovieWithPaginationAndSorting(
            @RequestParam (defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam (defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam (defaultValue = AppConstant.SORT_BY, required = false) String sortBy,
            @RequestParam (defaultValue = AppConstant.SORT_DIR, required = false) String dir
    ){
        return ResponseEntity.ok(movieService.getAllMovieWithPaginationAndSorting(pageNumber, pageSize, sortBy, dir));
    }

}
