package com.nhnacademy.shop.attachment.dto;

import lombok.Data;

@Data
public class FileUploadDto {
    private String fileUrl;
    private String fileType;

    public FileUploadDto(String fileUrl, String fileType) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }
}
