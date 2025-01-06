package com.hr_handlers.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public String uploadFile(String path, MultipartFile file) throws IOException {
        File convertedFile = convertMultipartFileToFile(file);
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String fullPath = path + "/" + fileName;

        amazonS3.putObject(new PutObjectRequest(bucketName, fullPath, convertedFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        convertedFile.delete();
        return amazonS3.getUrl(bucketName, fullPath).toString();
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileKey = extractFileKeyFromUrl(fileUrl);
            amazonS3.deleteObject(bucketName, fileKey);
            log.info("Deleted S3 file: {}", fileKey);
        } catch (Exception e) {
            log.error("Failed to delete S3 file: {}", fileUrl, e);
            throw new RuntimeException("S3 파일 삭제 실패: " + fileUrl);
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "-" + originalFileName;
    }

    private String extractFileKeyFromUrl(String fileUrl) {
        String urlPrefix = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/";
        if (fileUrl.startsWith(urlPrefix)) {
            return fileUrl.substring(urlPrefix.length());
        }
        throw new IllegalArgumentException("Invalid S3 file URL: " + fileUrl);
    }
}
