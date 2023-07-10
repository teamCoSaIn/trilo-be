package com.cosain.trilo.unit.trip.presentation.day.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.day.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.dto.factory.DayColorUpdateCommandFactory;
import com.cosain.trilo.trip.application.day.service.DayColorUpdateService;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.presentation.day.DayColorUpdateController;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
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

@WebMvcTest(DayColorUpdateController.class)
@DisplayName("Day 색상 수정 API DOCS 테스트")
public class DayColorUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private DayColorUpdateService dayColorUpdateService;

    @MockBean
    private DayColorUpdateCommandFactory dayColorUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 DayColor 수정 요청 -> 성공")
    public void dayColorUpdateDocsTest() throws Exception {
        mockingForLoginUserAnnotation();

        // given
        Long dayId = 1L;
        String rawColorName = "RED";
        DayColorUpdateRequest request = new DayColorUpdateRequest(rawColorName);

        DayColor dayColor = DayColor.of(rawColorName);
        DayColorUpdateCommand command = new DayColorUpdateCommand(dayColor);

        // mocking
        given(dayColorUpdateCommandFactory.createCommand(eq(rawColorName)))
                .willReturn(command);

        willDoNothing()
                .given(dayColorUpdateService)
                .updateDayColor(eq(dayId), any(), any(DayColorUpdateCommand.class));


        // when & then
        mockMvc.perform(put("/api/days/{dayId}/color", dayId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(dayId))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("dayId")
                                        .description("Day의 식별자(id)")
                        ),
                        requestFields(
                                fieldWithPath("colorName")
                                        .type(STRING)
                                        .description("변경할 색상 이름")
                                        .attributes(key("constraints").value("null일 수 없으며, 아래의 설명을 참고하여 가능한 색상 이름을 전달해주세요. (대소문자 구분 없음)"))
                        ),
                        responseFields(
                                fieldWithPath("dayId")
                                        .type(NUMBER)
                                        .description("Day의 식별자(id)")
                        )
                ));

        verify(dayColorUpdateCommandFactory, times(1)).createCommand(eq(rawColorName));
        verify(dayColorUpdateService, times(1)).updateDayColor(eq(dayId), any(), any(DayColorUpdateCommand.class));
    }

}
