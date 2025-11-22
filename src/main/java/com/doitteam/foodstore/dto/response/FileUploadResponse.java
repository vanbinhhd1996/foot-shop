package com.doitteam.foodstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String filename;
    private String originalName;
    private String url;
    private Long size;
    private String contentType;
    private String uploadedAt;
}