package com.moviestore.controller;

import com.moviestore.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@RestController
@RequestMapping("/file/")
public class FileController {

    // get the file path from application.yml
    @Value("${project.poster}")
    private String path;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException{

        // before uploading checking...
        // if selected-file already exists in posters-directory in project,
        // if already exists delete it.
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
            Files.deleteIfExists(Paths.get(path + File.separator + file.getOriginalFilename()));
        }

        String uploadedFileName = fileService.uploadFile(path, file);
        return ResponseEntity.ok("File '" + uploadedFileName + "' has been uploaded successfully!");
    }

    @GetMapping("/{fileName}")
    public void serverFileHandler(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourceFile = fileService.getSourceFile(path, fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, response.getOutputStream());
    }
}
