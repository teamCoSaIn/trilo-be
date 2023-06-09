package com.cosain.trilo.unit.trip.presentation.schedule.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleUpdateUseCase;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import com.cosain.trilo.trip.presentation.schedule.command.ScheduleUpdateController;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleUpdateRequest;
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

@WebMvcTest(ScheduleUpdateController.class)
@DisplayName("일정 수정 API DOCS 테스트")
public class ScheduleUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private ScheduleUpdateUseCase scheduleUpdateUseCase;

    @MockBean
    private ScheduleUpdateCommandFactory scheduleUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 일정 수정 요청 -> 성공")
    void scheduleUpdateDocTest() throws Exception {
        mockingForLoginUserAnnotation();


        // given
        mockingForLoginUserAnnotation();

        Long scheduleId = 1L;
        String rawTitle = "수정 일정제목";
        String rawContent = "수정 일정내용";
        ScheduleUpdateRequest request = new ScheduleUpdateRequest(rawTitle, rawContent);
        ScheduleUpdateCommand command = new ScheduleUpdateCommand(ScheduleTitle.of(rawContent), ScheduleContent.of(rawContent));

        given(scheduleUpdateCommandFactory.createCommand(eq(rawTitle), eq(rawContent))).willReturn(command);
        given(scheduleUpdateUseCase.updateSchedule(eq(scheduleId), any(), any(ScheduleUpdateCommand.class))).willReturn(1L);

        // when & then
        mockMvc.perform(put("/api/schedules/{scheduleId}", scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(1L))
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("scheduleId")
                                        .description("수정 여행 식별자(id)")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(STRING)
                                        .description("일정의 제목")
                                        .attributes(key("constraints").value("null 또는 공백일 수 없으며, 길이는 1-20자까지만 허용됩니다.")),
                                fieldWithPath("content")
                                        .type(STRING)
                                        .optional()
                                        .description("일정의 본문")
                        ),
                        responseFields(
                                fieldWithPath("scheduleId")
                                        .type(NUMBER)
                                        .description("일정의 식별자(id)")

                        )
                ));

        verify(scheduleUpdateCommandFactory).createCommand(eq(rawTitle), eq(rawContent));
        verify(scheduleUpdateUseCase).updateSchedule(eq(scheduleId), any(), any(ScheduleUpdateCommand.class));
    }
}