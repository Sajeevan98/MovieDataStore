package com.moviestore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // 1. get name of the file
        String fileName = file.getOriginalFilename();

        // 2. get the file path
        String filePath = path + File.separator + fileName;

        // 3. create file object & making 'poster' directory
        File fileObj = new File(path);
        if(!fileObj.exists()){
            fileObj.mkdir();
        }

        // 4. save file(poster/img) to the path(new poster directory)
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getSourceFile(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;

        return new FileInputStream(filePath);
    }
}
