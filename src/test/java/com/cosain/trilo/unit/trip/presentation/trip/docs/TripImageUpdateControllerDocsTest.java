package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_image_update.TripImageUpdateService;
import com.cosain.trilo.trip.presentation.trip.TripImageUpdateController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripImageUpdateController.class)
@DisplayName("여행 이미지 수정 API DOCS 테스트")
public class TripImageUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripImageUpdateService tripImageUpdateService;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("jpeg -> 성공")
    public void tripImageUpdateApiDocsTest() throws Exception {
        mockingForLoginUserAnnotation();
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpeg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        String imageURL = String.format("https://{이미지 파일 저장소 주소}/trips/%s/{이미지 파일명}.jpeg", tripId);
        given(tripImageUpdateService.updateTripImage(eq(tripId), any(), any(ImageFile.class)))
                .willReturn(imageURL);

        mockMvc.perform(multipart("/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andExpect(jsonPath("$.imageURL").value(imageURL))
                .andDo(restDocs.document(
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("이미지 수정할 여행(Trip)의 식별자(id)")
                        ),
                        requestParts(partWithName("image")
                                .description("올릴 이미지 파일")
                                .attributes(key("constraints").value("이미지를 필수로 전달해야합니다. 허용되는 이미지 타입(jpg, jpeg, png, gif, webp)"))
                        ),
                        responseFields(
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("수정된 여행(trip) 식별자(id)"),
                                fieldWithPath("imageURL")
                                        .type(STRING)
                                        .description("이미지가 저장된 URL(경로)")
                        )
                ));

        verify(tripImageUpdateService, times(1)).updateTripImage(eq(tripId), any(), any(ImageFile.class));
    }


}
