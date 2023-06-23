package com.cosain.trilo.unit.trip.presentation.day.query.docs;


import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.day.query.usecase.DaySearchUseCase;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.day.query.SingleDayQueryController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SingleDayQueryController.class)
public class SingleDayQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private DaySearchUseCase daySearchUseCase;
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Test
    void Day_단건_조회() throws Exception{

        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary1 = new ScheduleSummary(1L, "제목", "장소 이름","장소 식별자1", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(2L, "제목2", "장소 이름2","장소 식별자2", 33.33, 33.33);

        Long dayId = 1L;
        DayScheduleDetail dayScheduleDetail = new DayScheduleDetail(dayId, 1L, LocalDate.of(2023, 2, 3), DayColor.BLACK, List.of(scheduleSummary1, scheduleSummary2));
        given(daySearchUseCase.searchDeySchedule(eq(dayId))).willReturn(dayScheduleDetail);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/days"+"/{dayId}",dayId)
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
                                parameterWithName("dayId").description("조회할 Day ID")
                        ),
                        responseFields(
                                fieldWithPath("dayId").type(NUMBER).description("Day ID"),
                                fieldWithPath("tripId").type(NUMBER).description("여행 ID"),
                                fieldWithPath("date").type(STRING).description("여행 날짜"),
                                fieldWithPath("dayColor").type("DayColor").description("색상 정보 (하단 표 참고)"),
                                fieldWithPath("dayColor.name").ignored(),
                                fieldWithPath("dayColor.code").ignored(),
                                subsectionWithPath("schedules").type(ARRAY).description("일정 목록 (하단 표 참고)")
                        ),
                        responseFields(beneathPath("dayColor"),
                                fieldWithPath("name").type(STRING).description("색상 이름"),
                                fieldWithPath("code").type(STRING).description("색상 코드")
                        ),
                        responseFields(beneathPath("schedules").withSubsectionId("schedules"),
                                fieldWithPath("scheduleId").type(NUMBER).description("일정 ID"),
                                fieldWithPath("title").type(STRING).description("일정 제목"),
                                fieldWithPath("placeName").type(STRING).description("장소 이름"),
                                fieldWithPath("placeId").type(STRING).description("장소 ID"),
                                subsectionWithPath("coordinate").type(OBJECT).description("장소의 좌표 (하단 표 참고)")
                        ),
                        responseFields(beneathPath("schedules[].coordinate").withSubsectionId("coordinate"),
                                fieldWithPath("latitude").type(NUMBER).description("위도"),
                                fieldWithPath("longitude").type(NUMBER).description("경도")
                        )
                ));
    }
}
