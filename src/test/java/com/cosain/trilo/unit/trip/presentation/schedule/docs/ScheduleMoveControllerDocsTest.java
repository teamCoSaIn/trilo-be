package com.cosain.trilo.unit.trip.presentation.schedule.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveService;
import com.cosain.trilo.trip.presentation.schedule.ScheduleMoveController;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleMoveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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

/**
 * 일정 이동을 담당하는 Controller({@link ScheduleMoveController})의 문서화 테스트 코드 클래스입니다.
 * @see ScheduleMoveController
 */
@WebMvcTest(ScheduleMoveController.class)
@DisplayName("일정 이동 API DOCS 테스트")
public class ScheduleMoveControllerDocsTest extends RestDocsTestSupport {

    /**
     * ScheduleMoveController의 의존성
     */
    @MockBean
    private ScheduleMoveService scheduleMoveSerivce;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private static final String ACCESS_TOKEN = "Bearer accessToken";

    /**
     * <p>일정 이동 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증하고, 해당 API를 문서화합니다.</p>
     * <ul>
     *     <li>일정 이동이 성공됐다는 응답이 와야합니다. (200 OK, 본문 있음)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 일정 이동 요청 -> 성공")
    void scheduleMoveDocTest() throws Exception {
        // given
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);

        Long scheduleId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;

        var request = new ScheduleMoveRequest(targetDayId, targetOrder);
        var command = ScheduleMoveCommand.of(scheduleId, requestTripperId, targetDayId, targetOrder);
        ScheduleMoveResult moveResult = ScheduleMoveResult.builder()
                .scheduleId(scheduleId)
                .beforeDayId(1L)
                .afterDayId(targetDayId)
                .positionChanged(true)
                .build();

        given(scheduleMoveSerivce.moveSchedule(eq(command))).willReturn(moveResult);

        // when
        ResultActions resultActions = runTest(scheduleId, createJson(request));

        // then

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(scheduleId))
                .andExpect(jsonPath("$.beforeDayId").value(moveResult.getBeforeDayId()))
                .andExpect(jsonPath("$.afterDayId").value(moveResult.getAfterDayId()))
                .andExpect(jsonPath("$.positionChanged").value(moveResult.isPositionChanged()));

        // 내부 의존성 호출 검증
        verify(scheduleMoveSerivce, times(1)).moveSchedule(eq(command));

        // 문서화
        resultActions
                .andDo(restDocs.document(
                        // 요청 헤더 문서화
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        // 요청 경로변수 문서화
                        pathParameters(
                                parameterWithName("scheduleId")
                                        .description("이동할 여행 식별자(id)")
                        ),
                        // 요청 필드 문서화
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
                        // 응답 필드 문서화
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
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param scheduleId 일정 식별자(id)
     * @param content 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}/position", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
