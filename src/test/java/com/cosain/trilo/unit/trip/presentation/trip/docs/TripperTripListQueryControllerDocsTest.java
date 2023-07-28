package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.presentation.trip.TripperTripListQueryController;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripperTripListQueryController.class)
public class TripperTripListQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripListSearchService TripListSearchService;
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void 사용자_여행_목록_조회() throws Exception{
        mockingForLoginUserAnnotation();

        Long tripperId = 1L;
        Long tripId = 5L;
        int size = 3;
        TripListSearchResult.TripSummary tripSummary1 = new TripListSearchResult.TripSummary(4L, tripperId, "제목 1", TripStatus.DECIDED, LocalDate.of(2023, 3,4), LocalDate.of(2023, 4, 1), "image.jpg");
        TripListSearchResult.TripSummary tripSummary2 = new TripListSearchResult.TripSummary(3L, tripperId, "제목 2", TripStatus.UNDECIDED, null, null, "image.jpg");
        TripListSearchResult.TripSummary tripSummary3 = new TripListSearchResult.TripSummary(2L, tripperId, "제목 3", TripStatus.DECIDED, LocalDate.of(2023, 4,4), LocalDate.of(2023, 4, 5), "image.jpg");
        TripListQueryParam queryParam = TripListQueryParam.of(tripperId, tripId, size);
        TripListSearchResult searchResult = TripListSearchResult.of(true, List.of(tripSummary1, tripSummary2, tripSummary3));

        given(TripListSearchService.searchTripList(eq(queryParam))).willReturn(searchResult);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/trippers/{tripperId}/trips", tripperId)
                        .param("tripId", String.valueOf(tripId))
                        .param("size", String.valueOf(size))
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(searchResult.isHasNext()))
                .andExpect(jsonPath("$.trips").isNotEmpty())
                .andExpect(jsonPath("$.trips.[0].tripId").value(tripSummary1.getTripId()))
                .andExpect(jsonPath("$.trips.[1].tripId").value(tripSummary2.getTripId()))
                .andExpect(jsonPath("$.trips.[2].tripId").value(tripSummary3.getTripId()))
                .andExpect(jsonPath("$.trips.[0].title").value(tripSummary1.getTitle()))
                .andExpect(jsonPath("$.trips.[1].title").value(tripSummary2.getTitle()))
                .andExpect(jsonPath("$.trips.[2].title").value(tripSummary3.getTitle()))
                .andExpect(jsonPath("$.trips.[0].status").value(tripSummary1.getStatus()))
                .andExpect(jsonPath("$.trips.[1].status").value(tripSummary2.getStatus()))
                .andExpect(jsonPath("$.trips.[2].status").value(tripSummary3.getStatus()))
                .andExpect(jsonPath("$.trips.[0].startDate").value(tripSummary1.getStartDate().toString()))
                .andExpect(jsonPath("$.trips.[1].startDate").doesNotExist())
                .andExpect(jsonPath("$.trips.[2].startDate").value(tripSummary3.getStartDate().toString()))
                .andExpect(jsonPath("$.trips.[0].endDate").value(tripSummary1.getEndDate().toString()))
                .andExpect(jsonPath("$.trips.[1].endDate").doesNotExist())
                .andExpect(jsonPath("$.trips.[2].endDate").value(tripSummary3.getEndDate().toString()))
                .andExpect(jsonPath("$.trips.[0].imageURL").value(tripSummary1.getImageURL()))
                .andExpect(jsonPath("$.trips.[1].imageURL").value(tripSummary2.getImageURL()))
                .andExpect(jsonPath("$.trips.[2].imageURL").value(tripSummary3.getImageURL()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripperId").description("여행자 ID")
                        ),
                        queryParameters(
                                parameterWithName("tripId").optional().description("기준이 되는 여행 ID (하단 설명 참고)"),
                                parameterWithName("size").description("가져올 데이터의 개수")
                        ),
                        responseFields(
                                fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지 존재 여부"),
                                subsectionWithPath("trips").type(ARRAY).description("여행 목록")
                        )
                )).andDo(restDocs.document(
                        responseFields(beneathPath("trips").withSubsectionId("trips"),
                                fieldWithPath("tripId").type(NUMBER).description("여행 ID"),
                                fieldWithPath("tripperId").type(NUMBER).description("여행자 ID"),
                                fieldWithPath("title").type(STRING).description("여행 제목"),
                                fieldWithPath("status").type(STRING).description("여행 상태"),
                                fieldWithPath("startDate").type(STRING).description("여행 시작 날짜"),
                                fieldWithPath("endDate").type(STRING).description("여행 끝 날짜"),
                                fieldWithPath("imageURL").type(STRING).description("이미지가 저장된 URL(경로)")
                        )
                ));
    }
}
