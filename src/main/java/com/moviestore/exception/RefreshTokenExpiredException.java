package com.moviestore.exception;

public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException(String msg){
        super(msg);
    }
}
