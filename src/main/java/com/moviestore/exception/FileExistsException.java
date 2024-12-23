package com.moviestore.exception;

public class FileExistsException extends RuntimeException{

    public FileExistsException(String msg){
        super(msg);
    }
}
