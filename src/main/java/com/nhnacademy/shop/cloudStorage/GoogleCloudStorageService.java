package com.nhnacademy.shop.cloudStorage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.nhnacademy.shop.attachment.dto.FileUploadDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class GoogleCloudStorageService implements CloudStorageService {

    private final Storage storage;
    private static final String GCS_URL = "https://storage.googleapis.com";

    @Value("${gcp.gcs.bucket_name}")
    private String BUCKET_NAME;

    @Override
    public FileUploadDto uploadFile(MultipartFile file) {
        String fileType = file.getContentType();
        try {
            String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            BlobInfo blobInfo = BlobInfo.newBuilder(BUCKET_NAME, objectName)
                    .setContentType(file.getContentType())
                    .build();

            storage.createFrom(blobInfo, file.getInputStream());

            return new FileUploadDto(getFileUrl(objectName), fileType);
        } catch (IOException e) {
            throw new RuntimeException("GCS upload failed", e);
        }
    }

    @Override
    public List<FileUploadDto> uploadFiles(List<MultipartFile> files) {
        List<FileUploadDto> fileUploadDtos = new ArrayList<>();
        for (MultipartFile file: files) {
            fileUploadDtos.add(uploadFile(file));
        }
        return fileUploadDtos;
    }

    @Override
    public void removeFile(String fileUrl) {
        String objectName = extractObjectName(fileUrl);
        Blob blob = storage.get(BUCKET_NAME, objectName);
        BlobId idWithGeneration = blob.getBlobId();
        storage.delete(idWithGeneration);
    }

    @Override
    public void removeFiles(List<String> fileUrls) {
        for (String fileUrl: fileUrls) {
            removeFile(fileUrl);
        }
    }

    private String getFileUrl(String objectName) {
        return String.format("%s/%s/%s", GCS_URL, BUCKET_NAME, objectName);
    }

    private String extractObjectName(String fileUrl) {
        return fileUrl.split("/")[2];
    }
}
