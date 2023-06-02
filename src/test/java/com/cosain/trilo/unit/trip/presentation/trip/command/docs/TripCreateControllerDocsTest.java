package com.cosain.trilo.unit.trip.presentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.command.usecase.TripCreateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripCreateCommandFactory;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.command.TripCreateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripCreateController.class)
@DisplayName("여행 생성 API DOCS 테스트")
public class TripCreateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripCreateUseCase tripCreateUseCase;

    @MockBean
    private TripCreateCommandFactory tripCreateCommandFactory;

    private final String BASE_URL = "/api/trips";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 여행 생성 요청 -> 성공")
    void successTest() throws Exception {
        mockingForLoginUserAnnotation();

        String rawTitle = "제목";
        Long tripId = 1L;
        TripCreateRequest request = new TripCreateRequest(rawTitle);
        given(tripCreateCommandFactory.createCommand(eq(rawTitle))).willReturn(new TripCreateCommand(TripTitle.of(rawTitle)));
        given(tripCreateUseCase.createTrip(any(), any(TripCreateCommand.class))).willReturn(tripId);

        mockMvc.perform(post(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(STRING)
                                        .description("여행의 제목")
                                        .attributes(key("constraints").value("null 또는 공백일 수 없으며, 길이는 1-20자까지만 허용됩니다."))
                        ),
                        responseFields(
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("생성된 여행의 식별자(id)")
                        )
                ));
    }
}
