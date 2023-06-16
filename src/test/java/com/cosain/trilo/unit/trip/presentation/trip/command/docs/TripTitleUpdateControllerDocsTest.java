package com.cosain.trilo.unit.trip.presentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.command.usecase.TripTitleUpdateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripTitleUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripTitleUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.command.TripTitleUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripTitleUpdateRequest;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripTitleUpdateController.class)
@DisplayName("여행 수정 API DOCS 테스트")
public class TripTitleUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripTitleUpdateUseCase tripTitleUpdateUseCase;

    @MockBean
    private TripTitleUpdateCommandFactory tripTitleUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripTitle_with_authorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        String rawTitle = "변경할 제목";

        TripTitleUpdateRequest request = new TripTitleUpdateRequest(rawTitle);

        given(tripTitleUpdateCommandFactory.createCommand(eq(rawTitle)))
                .willReturn(new TripTitleUpdateCommand(TripTitle.of(rawTitle)));
        willDoNothing().given(tripTitleUpdateUseCase).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));

        mockMvc.perform(put("/api/trips/{tripId}/title", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("제목 수정할 여행 ID")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(STRING)
                                        .description("여행 제목")
                                        .attributes(key("constraints").value("null 또는 공백일 수 없으며, 길이는 1-20자까지만 허용됩니다."))
                        ),
                        responseFields(
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("수정된 여행 식별자(id)")
                        )
                ));

        verify(tripTitleUpdateUseCase, times(1)).updateTripTitle(eq(tripId), any(), any(TripTitleUpdateCommand.class));
        verify(tripTitleUpdateCommandFactory, times(1)).createCommand(eq(rawTitle));
    }
}