package com.hotsix.server.aws.controller;

import com.hotsix.server.aws.manager.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final AmazonS3Manager s3Manager;

    @PostMapping("/images")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> files) {
        List<String> urls = files.stream()
                .map(s3Manager::uploadFile)
                .toList();

        return ResponseEntity.ok(urls);
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(@RequestParam("url") String imageUrl) {
        s3Manager.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
