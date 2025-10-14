package com.hotsix.server.milestone.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/milestones/";

    //파일 저장

    public FileInfo storeFile(MultipartFile file, Long milestoneId) {
        try {
            Path dirPath = Paths.get(UPLOAD_DIR + "milestone_" + milestoneId);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new RuntimeException("파일명이 없습니다.");
            }
            String storedFileName = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = dirPath.resolve(storedFileName);

            file.transferTo(filePath.toFile());

            return new FileInfo(
                    originalFilename,
                    storedFileName,
                    filePath.toString(),
                    file.getSize(),
                    file.getContentType()
            );

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //파일 삭제

    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("파일 삭제 실패: " + filePath);
        }
    }

    // 파일 로드 (다운로드용)

    public Path loadFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + filePath);
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException("파일 로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 파일 정보를 담는 클래스

    public static class FileInfo {
        public final String originalName;   // 원본 파일명
        public final String storedName;     // 저장된 파일명 (UUID_원본명)
        public final String fullPath;       // 전체 경로
        public final long size;             // 파일 크기
        public final String contentType;    // MIME 타입

        public FileInfo(String originalName, String storedName, String fullPath,
                        long size, String contentType) {
            this.originalName = originalName;
            this.storedName = storedName;
            this.fullPath = fullPath;
            this.size = size;
            this.contentType = contentType;
        }
    }
}