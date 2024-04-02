package com.jisungin.infra.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class S3FileManager {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile multipartFiles, String dirName) {

        validateMultipartFile(multipartFiles);

        return this.uploadImage(multipartFiles, dirName);
    }

    public void removeFile(String fileName) {
        String key = getKeyFromImage(fileName);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (SdkClientException e) {
            throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    private String getKeyFromImage(String fileName) {
        try {
            URL url = new URL(fileName);
            String path = url.getPath();
            String desiredPart = path.replaceFirst("^/[^/]*/[^/]*/", "");
            if (desiredPart.startsWith("/")) { // 맨 앞이 "/"로 시작하는 경우 첫 번째 문자를 제거
                desiredPart = desiredPart.substring(1);
            }
            return URLDecoder.decode(desiredPart, "UTF-8");
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateMultipartFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || Objects.isNull(multipartFile.getOriginalFilename())) {
            throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    private String uploadImage(MultipartFile multipartFile, String dirName) {
        this.validateImageFileExtension(multipartFile.getOriginalFilename());
        try {
            return this.uploadImageToS3(multipartFile, dirName);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL);
        }
    }

    private String uploadImageToS3(MultipartFile multipartFile, String dirName) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String s3FileName = createS3FileName(originalFilename, dirName);

        InputStream is = multipartFile.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);
        metadata.setContentLength(bytes.length);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, s3FileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    private static String createS3FileName(String originalFilename, String dirName) {
        String s3FileName = dirName + "/" + UUID.randomUUID().toString().substring(0, 10) + originalFilename;
        return s3FileName;
    }

    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new BusinessException(ErrorCode.NOT_IMAGE);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException(ErrorCode.NOT_IMAGE);
        }
    }

}
