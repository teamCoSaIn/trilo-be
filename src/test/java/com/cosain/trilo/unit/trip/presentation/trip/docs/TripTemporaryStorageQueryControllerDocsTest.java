package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListQueryParam;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListSearchResult;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TemporarySearchService;
import com.cosain.trilo.trip.presentation.trip.TripTemporaryStorageQueryController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripTemporaryStorageQueryController.class)
public class TripTemporaryStorageQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TemporarySearchService temporarySearchService;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void 임시보관함_조회() throws Exception{

        // given
        Long tripId = 1L;
        Long scheduleId = 1L;
        int size = 2;
        mockingForLoginUserAnnotation();
        ScheduleSummary scheduleSummary1 = new ScheduleSummary(2L, "제목", "장소 이름","장소 식별자", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(3L, "제목", "장소 이름","장소 식별자",33.33, 33.33);

        var queryParam = TempScheduleListQueryParam.of(tripId, scheduleId, size);
        var result = TempScheduleListSearchResult.of(true, List.of(scheduleSummary1, scheduleSummary2));
        given(temporarySearchService.searchTemporary(eq(queryParam))).willReturn(result);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/trips/{tripId}/temporary-storage", tripId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .param("size", String.valueOf(size))
                .param("scheduleId", String.valueOf(scheduleId))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").isBoolean())
                .andExpect(jsonPath("$.tempSchedules").isNotEmpty())
                .andExpect(jsonPath("$.tempSchedules.size()").value(result.getTempSchedules().size()))
                .andExpect(jsonPath("$.tempSchedules[0].scheduleId").value(scheduleSummary1.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[1].scheduleId").value(scheduleSummary2.getScheduleId()))
                .andExpect(jsonPath("$.tempSchedules[0].title").value(scheduleSummary1.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[1].title").value(scheduleSummary2.getTitle()))
                .andExpect(jsonPath("$.tempSchedules[0].placeId").value(scheduleSummary1.getPlaceId()))
                .andExpect(jsonPath("$.tempSchedules[1].placeId").value(scheduleSummary2.getPlaceId()))
                .andExpect(jsonPath("$.tempSchedules[0].placeName").value(scheduleSummary1.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[1].placeName").value(scheduleSummary2.getPlaceName()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.latitude").value(scheduleSummary1.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.latitude").value(scheduleSummary2.getCoordinate().getLatitude()))
                .andExpect(jsonPath("$.tempSchedules[0].coordinate.longitude").value(scheduleSummary1.getCoordinate().getLongitude()))
                .andExpect(jsonPath("$.tempSchedules[1].coordinate.longitude").value(scheduleSummary2.getCoordinate().getLongitude()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        queryParameters(
                                parameterWithName("scheduleId").optional().description("기준이 되는 일정 ID (하단 설명 참고)"),
                                parameterWithName("size").description("가져올 데이터 개수")
                        ),
                        pathParameters(
                                parameterWithName("tripId").description("조회할 여행 ID")
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지 존재 여부"),
                                subsectionWithPath("tempSchedules").type(ARRAY).description("임시 일정 목록")
                        )
                )).andDo(restDocs.document(
                        responseFields(beneathPath("tempSchedules").withSubsectionId("tempSchedules"),
                                fieldWithPath("scheduleId").type(NUMBER).description("일정 ID"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("placeName").type(STRING).description("장소 이름"),
                                fieldWithPath("placeId").type(STRING).description("장소 식별자"),
                                subsectionWithPath("coordinate").type(OBJECT).description("장소의 좌표")
                        )
                )).andDo(restDocs.document(
                        responseFields(beneathPath("tempSchedules[].coordinate").withSubsectionId("coordinate"),
                                fieldWithPath("latitude").type(NUMBER).description("위도"),
                                fieldWithPath("longitude").type(NUMBER).description("경도")
                        )
                ));

        verify(temporarySearchService, times(1)).searchTemporary(eq(queryParam));
    }

}
