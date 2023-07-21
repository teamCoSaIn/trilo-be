package com.cosain.trilo.unit.trip.presentation.schedule.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateService;
import com.cosain.trilo.trip.presentation.schedule.ScheduleCreateController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleCreateController.class)
@DisplayName("일정 생성 API DOCS 테스트")
public class ScheduleCreateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private ScheduleCreateService scheduleCreateService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 일정 생성 요청 -> 성공")
    void scheduleCreateDocTest() throws Exception {
        long requestTripperId = 1L;
        mockingForLoginUserAnnotation(requestTripperId);
        Long tripId = 2L;
        Long dayId = 3L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-Name";
        Double latitude = 37.564213;
        Double longitude = 127.001698;

        String requestJson = String.format("""
                {
                    "dayId": %d,
                    "tripId": %d,
                    "title": "%s",
                    "placeId": "%s",
                    "placeName": "%s",
                    "coordinate": {
                        "latitude": %f,
                        "longitude": %f
                    }
                }
                """, dayId, tripId, rawScheduleTitle, placeId, placeName, latitude, longitude);

        var command = ScheduleCreateCommand.of(requestTripperId, tripId, dayId, rawScheduleTitle, placeId, placeName, latitude, longitude);

        Long createdScheduleId = 3L;
        given(scheduleCreateService.createSchedule(eq(command))).willReturn(createdScheduleId);


        mockMvc.perform(post("/api/schedules")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(requestJson)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").value(createdScheduleId))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("dayId")
                                        .type(NUMBER)
                                        .optional()
                                        .description("day의 식별자. null 일 경우 임시보관함으로 간주"),
                                fieldWithPath("title")
                                        .type(STRING)
                                        .description("일정의 제목")
                                        .attributes(key("constraints").value("null일 수 없으며, 길이는 20자 이하까지 허용됩니다. (공백, 빈 문자열 허용)")),
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("소속된 여행(Trip)의 식별자")
                                        .attributes(key("constraints").value("null 이 허용되지 않습니다.")),
                                fieldWithPath("placeId")
                                        .type(STRING)
                                        .optional()
                                        .description("장소의 google map api 기준 식별자"),
                                fieldWithPath("placeName")
                                        .type(STRING)
                                        .optional()
                                        .description("장소명"),
                                fieldWithPath("coordinate")
                                        .type("Coordinate")
                                        .description("장소의 좌표. 하위 표를 참고하세요.")
                                        .attributes(key("constraints").value("null 이 허용되지 않습니다.")),
                                fieldWithPath("coordinate.latitude").ignored(),
                                fieldWithPath("coordinate.longitude").ignored()
                        ),
                        responseFields(
                                fieldWithPath("scheduleId")
                                        .type(NUMBER)
                                        .description("생성된 일정 식별자(id)")
                        )
                ))
                .andDo(restDocs.document(
                        requestFields(beneathPath("coordinate"),
                                fieldWithPath("latitude")
                                        .type(NUMBER)
                                        .description("위도")
                                        .attributes(key("constraints").value("null이여선 안 되고 -90 이상 90 이하까지만 허용")),
                                fieldWithPath("longitude")
                                        .type(NUMBER)
                                        .description("경도")
                                        .attributes(key("constraints").value("null이여선 안 되고 -180 이상 180 이하까지만 허용"))
                        )
                ));

        verify(scheduleCreateService, times(1)).createSchedule(eq(command));
    }
}
