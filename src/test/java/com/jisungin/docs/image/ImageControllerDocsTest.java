package com.jisungin.docs.image;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.image.ImageController;
import com.jisungin.application.image.ImageService;
import com.jisungin.docs.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

public class ImageControllerDocsTest extends RestDocsSupport {

    private final ImageService imageService = mock(ImageService.class);

    @Override
    protected Object initController() {
        return new ImageController(imageService);
    }

    @Test
    @DisplayName("이미지를 업로드 하는 API")
    void uploadImage() throws Exception {
        String fileName = "image.jpg";
        byte[] imageByte = "image".getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("files", fileName, "image/jpg", imageByte);

        String dirName = "dirName";

        given(imageService.upload(anyList(), anyString()))
                .willReturn(List.of("image.jpg"));

        mockMvc.perform(multipart("/v1/s3")
                        .file(multipartFile)
                        .param("dirName", dirName)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("image/upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("files").description("이미지")
                        ),
                        formParameters(
                                parameterWithName("dirName")
                                        .description("디렉토리명 (ex: talkroom, comment, review ...)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL")
                        )
                ));
    }

    @Test
    @DisplayName("이미지를 삭제하는 API")
    void removeImage() throws Exception {
        String fileName = "fileName";

        mockMvc.perform(delete("/v1/s3")
                        .param("fileName", fileName)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("image/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        formParameters(
                                parameterWithName("fileName")
                                        .description("이미지 URL")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("return data null")
                        )
                ));
    }

}
