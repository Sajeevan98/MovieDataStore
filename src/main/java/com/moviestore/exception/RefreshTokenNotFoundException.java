package com.moviestore.exception;

public class RefreshTokenNotFoundException extends RuntimeException{

    public RefreshTokenNotFoundException(String msg){
        super(msg);
    }
}
