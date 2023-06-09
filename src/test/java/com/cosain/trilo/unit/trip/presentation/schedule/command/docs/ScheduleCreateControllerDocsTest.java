package com.cosain.trilo.unit.trip.presentation.schedule.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleCreateUseCase;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleCreateCommandFactory;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import com.cosain.trilo.trip.presentation.schedule.command.ScheduleCreateController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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
    private ScheduleCreateUseCase scheduleCreateUseCase;

    @MockBean
    private ScheduleCreateCommandFactory scheduleCreateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 일정 생성 요청 -> 성공")
    void scheduleCreateDocTest() throws Exception {
        mockingForLoginUserAnnotation();

        Long dayId = 1L;
        Long tripId = 1L;
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

        ScheduleCreateCommand command = ScheduleCreateCommand.builder()
                .dayId(dayId)
                .tripId(tripId)
                .scheduleTitle(ScheduleTitle.of(rawScheduleTitle))
                .place(Place.of(placeId, placeName, Coordinate.of(latitude, longitude)))
                .build();

        Long createdScheduleId = 3L;

        given(scheduleCreateCommandFactory.createCommand(
                eq(dayId), eq(tripId), eq(rawScheduleTitle),
                eq(placeId), eq(placeName),
                eq(latitude), eq(longitude), anyList())).willReturn(command);
        given(scheduleCreateUseCase.createSchedule(any(), any(ScheduleCreateCommand.class))).willReturn(createdScheduleId);


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
                                        .attributes(key("constraints").value("null 또는 공백일 수 없으며, 길이는 1-20자까지만 허용됩니다.")),
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

        verify(scheduleCreateUseCase).createSchedule(any(), any(ScheduleCreateCommand.class));
        verify(scheduleCreateCommandFactory).createCommand(
                eq(dayId), eq(tripId), eq(rawScheduleTitle),
                eq(placeId), eq(placeName),
                eq(latitude), eq(longitude), anyList()
        );
    }
}
