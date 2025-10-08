package com.hotsix.server.aws.controller;

import com.hotsix.server.aws.manager.AmazonS3Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping(value = "/images", consumes = "multipart/form-data")
    @Operation(summary = "이미지 업로드", description = "여러 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공")
    })
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> files) {
        List<String> urls = files.stream()
                .map(s3Manager::uploadFile)
                .toList();

        return ResponseEntity.ok(urls);
    }

    @DeleteMapping("/images")
    @Operation(summary = "이미지 업로드", description = "이미지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 삭제 성공")
    })
    public ResponseEntity<Void> deleteImage(@RequestParam("url") String imageUrl) {
        s3Manager.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
