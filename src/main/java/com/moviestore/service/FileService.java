package com.moviestore.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public interface FileService {

    String uploadFile(String path, MultipartFile file) throws IOException;

    // before saving the file, we need to convert the file as String OR byte.
    // for generate url...
    InputStream getSourceFile(String path, String fileName) throws FileNotFoundException;

}
