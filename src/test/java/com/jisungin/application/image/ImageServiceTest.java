package com.jisungin.application.image;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.jisungin.infra.s3.S3FileManager;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Mock
    S3FileManager s3FileManager;

    @Test
    @DisplayName("이미지를 업로드 테스트")
    void uploadImage() throws Exception {
        // given
        MockMultipartFile multipartFile = getMockMultipartFile();

        given(s3FileManager.upload(any(MultipartFile.class), anyString()))
                .willReturn("image.jpg");
        // when
        List<String> images = imageService.upload(List.of(multipartFile), "talkroom");

        // then
        Assertions.assertThat("image.jpg").isEqualTo(images.get(0));
    }

    private static MockMultipartFile getMockMultipartFile() {
        String fileName = "image.jpg";
        byte[] imageByte = "image".getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "image", imageByte);
        return multipartFile;
    }

}