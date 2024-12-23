package com.moviestore.exception;

public class MovieNotFoundException extends RuntimeException{

    public MovieNotFoundException(String msg){
        super(msg);
    }
}
