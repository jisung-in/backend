package com.jisungin.application.image;

import com.jisungin.infra.s3.S3FileManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final S3FileManager s3FileManager;

    public List<String> upload(List<MultipartFile> multipartFiles, String dirName) {
        return multipartFiles.stream()
                .map(multipartFile -> s3FileManager.upload(multipartFile, dirName))
                .collect(Collectors.toList());
    }

    public void removeFile(String fileName) {
        s3FileManager.removeFile(fileName);
    }

}
