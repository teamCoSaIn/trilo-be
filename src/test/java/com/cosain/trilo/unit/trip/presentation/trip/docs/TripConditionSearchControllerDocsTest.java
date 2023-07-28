package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripConditionSearchService;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.presentation.trip.TripConditionSearchController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripConditionSearchController.class)
public class TripConditionSearchControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripConditionSearchService tripConditionSearchService;

    private static final String BASE_URL = "/api/trips";

    @Test
    void 여행_목록_조회() throws Exception{

        // given
        int size = 5;
        String query = "제주";
        Long tripId = 1L;
        String imageURL = "https://.../image.jpg";
        TripSearchRequest.SortType sortType = TripSearchRequest.SortType.RECENT;
        TripSearchResponse.TripSummary tripSummary1 = new TripSearchResponse.TripSummary(2L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주도 여행", imageURL);
        TripSearchResponse.TripSummary tripSummary2 = new TripSearchResponse.TripSummary(1L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주 가보자", imageURL);
        TripSearchResponse tripSearchResponse = new TripSearchResponse(true, List.of(tripSummary1, tripSummary2));

        given(tripConditionSearchService.findBySearchConditions(any(TripSearchRequest.class))).willReturn(tripSearchResponse);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                        .param("sortType", String.valueOf(sortType))
                        .param("query", query)
                        .param("tripId", String.valueOf(tripId))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("tripId").optional().description("기준이 되는 여행 ID (하단 설명 참고)"),
                                parameterWithName("size").description("가져올 데이터의 개수"),
                                parameterWithName("sortType").description("정렬 기준 ex) 최신순 (RECENT), 좋아요 많은 순 (LIKE)"),
                                parameterWithName("query").description("검색어")
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
                                fieldWithPath("period").type(NUMBER).description("여행 기간"),
                                fieldWithPath("imageURL").type(STRING).description("이미지가 저장된 URL(경로)")
                        )
                ));
    }
}
