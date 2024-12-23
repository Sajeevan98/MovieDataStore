package com.moviestore.service;

import com.moviestore.dto.MovieDto;
import com.moviestore.dto.MoviePageResponse;
import com.moviestore.entities.Movie;
import com.moviestore.exception.FileExistsException;
import com.moviestore.exception.MovieNotFoundException;
import com.moviestore.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
public class MovieServiceImpl implements MovieService {

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    private final MovieRepository movieRepository;
    private final FileService fileService;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }


    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. upload the file
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
             throw new FileExistsException(file.getOriginalFilename() + ": file already exists! try another file.");
            // OR
            // If select-file(for uploading) already exits in poster-directory, then delete & re-upload...
//            Files.deleteIfExists(Paths.get(path + File.separator + file.getOriginalFilename()));
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. set the new-file-name to poster field in movieDto class.
        movieDto.setPoster(uploadedFileName);

        // 3. map Dto object to Movie Object
        Movie movieObj = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMusicDirector(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 4. saved the Movie object
        Movie objSave = movieRepository.save(movieObj);

        // 5. generate the poster-url
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        // 6. map Movie object to Dto object & return it
        MovieDto response = new MovieDto(

                objSave.getMovieId(),
                objSave.getTitle(),
                objSave.getDirector(),
                objSave.getStudio(),
                objSave.getMusicDirector(),
                objSave.getMovieCast(),
                objSave.getReleaseYear(),
                objSave.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. check the DB, if data exists with given id
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie Not Found with Id: " + movieId));

        // for my checking purpose...
        System.out.println("*Movie Id: "+movie.getMovieId()+ "  *Movie Name: "+movie.getTitle());

        // 2. generate poster-url
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 3. map to MovieDto & return it
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMusicDirector(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. fetch all data from DB.
        List<Movie> movieList = movieRepository.findAll();

        // 2. create List for MovieDto.
        List<MovieDto> movieDtoList = new ArrayList<>();

        // 3. iterate the List by forEach loop, and generate poster-url for each movie-poster.
        // after that, map to MovieDto object
        for(Movie m: movieList){

            String posterUrl = baseUrl + "/file/" + m.getPoster();

            MovieDto movieDto = new MovieDto(
                    m.getMovieId(),
                    m.getTitle(),
                    m.getDirector(),
                    m.getStudio(),
                    m.getMusicDirector(),
                    m.getMovieCast(),
                    m.getReleaseYear(),
                    m.getPoster(),
                    posterUrl
            );
            movieDtoList.add(movieDto);
        }
        return movieDtoList;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. check the data in DB, if exists with given id.
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie Not Found with Id: " + movieId));

        // 2. if file is not existing, do nothing.
        // if existing, then delete the file(poster) from posters-directory, upload new as new-file.
        String fileName = movie.getPoster();

        if(file != null){
            if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
                Files.deleteIfExists(Paths.get(path + File.separator + file.getOriginalFilename()));
            }
            fileName = fileService.uploadFile(path, file);
        }

        // 3. if file-field is Null(Not providing), Then assign/set the existing value(poster/img.png) which is on the DB.
        //    if poster value providing(which means not-Null), set the new-poster value(after the if condition process)
        movieDto.setPoster(fileName);

        // 4. map to Movie object
        Movie mData = new Movie(
                movie.getMovieId(), // get existing movieId for Updating.
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMusicDirector(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 5. saved the Movie object
        Movie updateMovie = movieRepository.save(mData);

        // 6. generate poster-url
        String posterUrl = baseUrl + "/file/" + fileName;

        // 7. map to MovieDto object
        MovieDto mDto = new MovieDto(
                mData.getMovieId(),
                mData.getTitle(),
                mData.getDirector(),
                mData.getStudio(),
                mData.getMusicDirector(),
                mData.getMovieCast(),
                mData.getReleaseYear(),
                mData.getPoster(),
                posterUrl
        );
        return mDto;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. check the data in DB, if exists with given id.
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie Not Found with Id: " + movieId));

        // 2. delete poster/file associated with given id.
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        // 3. delete the movie object.
        movieRepository.delete(movie);

        Integer id = movie.getMovieId();
        return "movie deleted successfully with Id: "+ id;
    }

    @Override
    public MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageAble = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePages = movieRepository.findAll(pageAble);
        List<Movie> movieList = moviePages.getContent();

        // 2. create List for MovieDto.
        List<MovieDto> movieDtoList = new ArrayList<>();

        // 3. iterate the List by forEach loop, and generate poster-url for each movie-poster.
        // after that, map to MovieDto object
        for(Movie m: movieList){

            String posterUrl = baseUrl + "/file/" + m.getPoster();

            MovieDto movieDto = new MovieDto(
                    m.getMovieId(),
                    m.getTitle(),
                    m.getDirector(),
                    m.getStudio(),
                    m.getMusicDirector(),
                    m.getMovieCast(),
                    m.getReleaseYear(),
                    m.getPoster(),
                    posterUrl
            );
            movieDtoList.add(movieDto);
        }
        return new MoviePageResponse(movieDtoList, pageNumber, pageSize, moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                : Sort.by(sortBy).descending();

        Pageable pageAble = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePages = movieRepository.findAll(pageAble);
        List<Movie> movieList = moviePages.getContent();

        // 2. create List for MovieDto.
        List<MovieDto> movieDtoList = new ArrayList<>();

        // 3. iterate the List by forEach loop, and generate poster-url for each movie-poster.
        // after that, map to MovieDto object
        for(Movie m: movieList){

            String posterUrl = baseUrl + "/file/" + m.getPoster();

            MovieDto movieDto = new MovieDto(
                    m.getMovieId(),
                    m.getTitle(),
                    m.getDirector(),
                    m.getStudio(),
                    m.getMusicDirector(),
                    m.getMovieCast(),
                    m.getReleaseYear(),
                    m.getPoster(),
                    posterUrl
            );
            movieDtoList.add(movieDto);
        }
        return new MoviePageResponse(movieDtoList, pageNumber, pageSize, (int) moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());
    }

}
