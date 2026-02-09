package com.nhnacademy.shop.cloudStorage;


import com.nhnacademy.shop.attachment.dto.FileUploadDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@Service
public interface CloudStorageService {
    FileUploadDto uploadFile(MultipartFile file);
    List<FileUploadDto> uploadFiles(List<MultipartFile> files);

    void removeFile(String fileUrl);
    void removeFiles(List<String> fileUrls);


}
