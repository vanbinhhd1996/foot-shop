package com.doitteam.foodstore.service;

import com.doitteam.foodstore.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("アップロードディレクトリを作成しました: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("ファイル保存用のディレクトリを作成できませんでした", ex);
        }
    }

    /**
     * ファイルを保存
     */
    public String storeFile(MultipartFile file) {
        // ファイル検証
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);

        // ユニークなファイル名を生成
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;

        try {
            // ファイル名にパス区切り文字が含まれていないかチェック
            if (originalFilename.contains("..")) {
                throw new BadRequestException("ファイル名に不正な文字が含まれています: " + originalFilename);
            }

            // ファイルを保存
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("ファイルを保存しました: {}", newFilename);
            return newFilename;

        } catch (IOException ex) {
            throw new RuntimeException("ファイル " + newFilename + " を保存できませんでした", ex);
        }
    }

    /**
     * 複数ファイルを保存
     */
    public String[] storeFiles(MultipartFile[] files) {
        String[] fileNames = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            fileNames[i] = storeFile(files[i]);
        }

        return fileNames;
    }

    /**
     * ファイルを削除
     */
    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
            log.info("ファイルを削除しました: {}", filename);
        } catch (IOException ex) {
            log.error("ファイルを削除できませんでした: {}", filename, ex);
            throw new RuntimeException("ファイルを削除できませんでした: " + filename, ex);
        }
    }

    /**
     * ファイルの存在確認
     */
    public boolean fileExists(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            return Files.exists(filePath);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * ファイルパスを取得
     */
    public Path getFilePath(String filename) {
        return this.fileStorageLocation.resolve(filename).normalize();
    }

    /**
     * ファイル検証
     */
    private void validateFile(MultipartFile file) {
        // 空ファイルチェック
        if (file.isEmpty()) {
            throw new BadRequestException("ファイルが空です");
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        // ファイル名に不正な文字が含まれていないかチェック
        if (filename.contains("..")) {
            throw new BadRequestException("ファイル名に不正なパス文字列が含まれています: " + filename);
        }

        // ファイルサイズチェック (10MB)
        long maxFileSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("ファイルサイズが大きすぎます。最大10MBまでです");
        }

        // 拡張子チェック
        String extension = getFileExtension(filename).toLowerCase();
        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};

        boolean isValid = false;
        for (String ext : allowedExtensions) {
            if (extension.equals(ext)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new BadRequestException("許可されていないファイル形式です。JPG、JPEG、PNG、GIF、WEBPのみアップロード可能です");
        }
    }

    /**
     * ファイル拡張子を取得
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }

        return filename.substring(dotIndex + 1);
    }
}
