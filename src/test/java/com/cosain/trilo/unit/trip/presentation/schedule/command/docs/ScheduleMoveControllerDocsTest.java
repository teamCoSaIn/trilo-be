package com.cosain.trilo.unit.trip.presentation.schedule.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleMoveUseCase;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleMoveCommandFactory;
import com.cosain.trilo.trip.presentation.schedule.command.ScheduleMoveController;
import com.cosain.trilo.trip.presentation.schedule.command.dto.request.ScheduleMoveRequest;
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
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleMoveController.class)
@DisplayName("일정 이동 API DOCS 테스트")
public class ScheduleMoveControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private ScheduleMoveUseCase scheduleMoveUseCase;

    @MockBean
    private ScheduleMoveCommandFactory scheduleMoveCommandFactory;

    private static final String ACCESS_TOKEN = "Bearer accessToken";
    private static final String ENDPOINT_URL_TEMPLATE = "/api/schedules/{scheduleId}/position";

    @Test
    @DisplayName("인증된 사용자의 일정 이동 요청 -> 성공")
    void scheduleMoveDocTest() throws Exception {
        mockingForLoginUserAnnotation();

        // given & mocking
        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        ScheduleMoveRequest request = new ScheduleMoveRequest(targetDayId, targetOrder);
        ScheduleMoveCommand command = new ScheduleMoveCommand(targetDayId, targetOrder);
        ScheduleMoveResult moveResult = ScheduleMoveResult.builder()
                .scheduleId(scheduleId)
                .beforeDayId(1L)
                .afterDayId(targetDayId)
                .positionChanged(true)
                .build();

        given(scheduleMoveCommandFactory.createCommand(eq(targetDayId), eq(targetOrder)))
                .willReturn(command);
        given(scheduleMoveUseCase.moveSchedule(eq(scheduleId), any(), any(ScheduleMoveCommand.class)))
                .willReturn(moveResult);

        // when & then
        mockMvc.perform(put(ENDPOINT_URL_TEMPLATE, scheduleId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId))
                .andExpect(jsonPath("$.beforeDayId").value(moveResult.getBeforeDayId()))
                .andExpect(jsonPath("$.afterDayId").value(moveResult.getAfterDayId()))
                .andExpect(jsonPath("$.positionChanged").value(moveResult.isPositionChanged()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("scheduleId")
                                        .description("이동할 여행 식별자(id)")
                        ),
                        requestFields(
                                fieldWithPath("targetDayId")
                                        .type(NUMBER)
                                        .description("일정의 옮겨질 위치 Day 식별자(null일 경우 임시보관함)")
                                        .optional(),
                                fieldWithPath("targetOrder")
                                        .type(NUMBER)
                                        .description("일정을 삽입할 위치(0,1,2,3, ...). 위의 보충 설명 참고")
                                        .attributes(key("constraints").value("null일 수 없고, 0 이상이여야 함. 해당 Day 상에서의 가능한 순서 범위를 벗어나선 안 됨"))
                        ),
                        responseFields(
                                fieldWithPath("scheduleId")
                                        .type(NUMBER)
                                        .description("일정의 식별자(id)"),
                                fieldWithPath("beforeDayId")
                                        .type(NUMBER)
                                        .description("일정이 이동하기 전의 Day 식별자(id)"),
                                fieldWithPath("afterDayId")
                                        .type(NUMBER)
                                        .description("일정이 이동한 후의 Day 식별자(id)"),
                                fieldWithPath("positionChanged")
                                        .type(BOOLEAN)
                                        .description("일정의 위치(Day, 상대적 순서) 변경 여부. 일정의 위치가 변경됐을 경우 true, 제자리 그대로일 경우 false")
                        )
                ));

        verify(scheduleMoveCommandFactory).createCommand(eq(targetDayId), eq(targetOrder));
        verify(scheduleMoveUseCase).moveSchedule(eq(scheduleId), any(), any(ScheduleMoveCommand.class));
    }
}
