package com.cosain.trilo.unit.trip.presentation.day.query.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.day.query.usecase.DaySearchUseCase;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.infra.dto.Coordinate;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.day.query.TripDayListQueryController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripDayListQueryController.class)
public class TripDayListQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private DaySearchUseCase daySearchUseCase;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void Day_목록_조회() throws Exception {
        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary = new ScheduleSummary(1L, "제목", "장소 이름", "장소 식별자", 33.33, 33.33);
        DayScheduleDetail dayScheduleDetail = new DayScheduleDetail(1L, 1L, LocalDate.of(2023, 5, 13), DayColor.BLACK, List.of(scheduleSummary));
        List<DayScheduleDetail> dayScheduleDetails = List.of(dayScheduleDetail);

        given(daySearchUseCase.searchDaySchedules(tripId)).willReturn(dayScheduleDetails);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/trips/{tripId}/days", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId").description("조회할 Trip ID")
                        ),
                        responseFields(
                                subsectionWithPath("days").type(ARRAY).description("Day 목록")
                        ),
                        responseFields(beneathPath("days").withSubsectionId("days"),
                                fieldWithPath("dayId").type(NUMBER).description("Day ID"),
                                fieldWithPath("tripId").type(NUMBER).description("여행 ID"),
                                fieldWithPath("date").description(STRING).description("여행 날짜"),
                                subsectionWithPath("dayColor").type("DayColor").description("색상 정보 (하단 표 참고)"),
                                subsectionWithPath("schedules").type("Schedule[]").description("일정 목록 (하단 표 참고)")
                        ),
                        responseFields(beneathPath("days[].dayColor").withSubsectionId("dayColor"),
                                fieldWithPath("name").type(STRING).description("색상 이름"),
                                fieldWithPath("code").type(STRING).description("색상 코드")
                        ),
                        responseFields(beneathPath("days[].schedules").withSubsectionId("schedules"),
                                fieldWithPath("[].scheduleId").type(NUMBER).description("일정 ID"),
                                fieldWithPath("[].title").type(STRING).description("일정 제목"),
                                fieldWithPath("[].placeName").type(STRING).description("장소 이름"),
                                fieldWithPath("[].placeId").type(STRING).description("장소 ID"),
                                subsectionWithPath("[].coordinate").type("Coordinate").description("장소의 좌표 (하단 표 참고)")
                        ),
                        responseFields(beneathPath("days[].schedules[].coordinate").withSubsectionId("coordinate"),
                                fieldWithPath("latitude").type(NUMBER).description("위도"),
                                fieldWithPath("longitude").type(NUMBER).description("경도")
                        )
                ));

    }
}
