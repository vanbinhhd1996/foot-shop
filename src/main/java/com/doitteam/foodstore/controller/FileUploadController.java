package com.doitteam.foodstore.controller;


import com.doitteam.foodstore.dto.response.ApiResponse;
import com.doitteam.foodstore.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class FileUploadController {

    private final FileStorageService fileStorageService;

    /**
     * 単一ファイルアップロード
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        log.info("ファイルアップロードリクエスト: {}", file.getOriginalFilename());

        String filename = fileStorageService.storeFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("filename", filename);
        response.put("originalName", file.getOriginalFilename());
        response.put("url", "/api/files/" + filename);
        response.put("size", String.valueOf(file.getSize()));
        response.put("contentType", file.getContentType());

        return ResponseEntity.ok(ApiResponse.success("ファイルのアップロードに成功しました", response));
    }

    /**
     * 複数ファイルアップロード
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files) {

        log.info("複数ファイルアップロードリクエスト: {} files", files.length);

        String[] fileNames = fileStorageService.storeFiles(files);

        Map<String, Object> response = new HashMap<>();
        response.put("count", files.length);
        response.put("files", fileNames);

        return ResponseEntity.ok(ApiResponse.success("ファイルのアップロードに成功しました", response));
    }

    /**
     * ファイルダウンロード
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename,
            HttpServletRequest request) {

        try {
            Path filePath = fileStorageService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // ファイルのContent-Typeを判定
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.info("Could not determine file type.");
            }

            // デフォルトのContent-Type
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception ex) {
            log.error("ファイルダウンロードエラー", ex);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ファイル削除
     */
    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String filename) {

        log.info("ファイル削除リクエスト: {}", filename);

        if (!fileStorageService.fileExists(filename)) {
            return ResponseEntity.notFound().build();
        }

        fileStorageService.deleteFile(filename);

        return ResponseEntity.ok(ApiResponse.success("ファイルを削除しました", null));
    }

    /**
     * ファイル存在チェック
     */
    @GetMapping("/check/{filename:.+}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkFile(@PathVariable String filename) {

        boolean exists = fileStorageService.fileExists(filename);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}