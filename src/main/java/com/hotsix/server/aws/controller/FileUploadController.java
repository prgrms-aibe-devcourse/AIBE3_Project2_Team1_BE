package com.hotsix.server.aws.controller;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.hotsix.server.aws.manager.AmazonS3Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final AmazonS3Manager s3Manager;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/images", consumes = "multipart/form-data")
    @Operation(summary = "이미지 업로드", description = "여러 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공")
    })
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> files) {

        if (files.size() > 10) {
            throw new IllegalArgumentException("최대 10개까지 업로드 가능합니다");
        }

        files.forEach(file -> {
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다");
            }
            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다");
            }
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다");
            }
        });

        List<String> urls = files.stream()
                .map(s3Manager::uploadFile)
                .toList();

        return ResponseEntity.ok(urls);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/images")
    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "이미지 삭제 성공")
    })
    public ResponseEntity<Void> deleteImage(@RequestParam("url") String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("https://") || !imageUrl.contains(".s3.")) {
            throw new IllegalArgumentException("유효하지 않은 S3 URL입니다");
        }
        s3Manager.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/presigned")
    @Operation(summary = "파일 ")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String fileName) {
        String presignedUrl = s3Manager.getPresignedUrl(fileName);
        return ResponseEntity.ok(presignedUrl);
    }

}