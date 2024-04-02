package com.jisungin.api.image;

import com.jisungin.api.ApiResponse;
import com.jisungin.application.image.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/v1")
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/s3/upload")
    public ApiResponse<List<String>> upload(@RequestPart List<MultipartFile> files, @RequestParam String dirName) {
        return ApiResponse.ok(imageService.upload(files, dirName));
    }

    @DeleteMapping("/s3/remove")
    public ApiResponse<Void> removeFile(@RequestParam String fileName) {
        imageService.removeFile(fileName);

        return ApiResponse.<Void>builder()
                .message("삭제 성공")
                .status(HttpStatus.OK)
                .build();
    }

}
