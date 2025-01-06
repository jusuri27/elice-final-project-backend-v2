package com.hr_handlers.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class FileUploadRequestDto {
    private String path;
    private MultipartFile file;
}