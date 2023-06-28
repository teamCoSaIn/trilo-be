package com.cosain.trilo.unit.trip.presentation.trip.query.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.query.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.presentation.trip.query.TripperTripListQueryController;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripperTripListQueryController.class)
public class TripperTripListQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripListSearchUseCase tripListSearchUseCase;
    private final String BASE_URL = "/api/trips";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void 사용자_여행_목록_조회() throws Exception{
        mockingForLoginUserAnnotation();

        Long tripperId = 1L;
        Long tripId = 5L;
        int size = 3;
        TripSummary tripSummary1 = new TripSummary(4L, tripperId, "제목 1", TripStatus.DECIDED, LocalDate.of(2023, 3,4), LocalDate.of(2023, 4, 1), "image.jpg");
        TripSummary tripSummary2 = new TripSummary(3L, tripperId, "제목 2", TripStatus.UNDECIDED, null, null, "image.jpg");
        TripSummary tripSummary3 = new TripSummary(2L, tripperId, "제목 3", TripStatus.DECIDED, LocalDate.of(2023, 4,4), LocalDate.of(2023, 4, 5), "image.jpg");
        Pageable pageable = PageRequest.ofSize(size);
        SliceImpl<TripSummary> tripDetails = new SliceImpl<>(List.of(tripSummary1, tripSummary2, tripSummary3), pageable, true);

        given(tripListSearchUseCase.searchTripSummaries(any(TripPageCondition.class), eq(pageable))).willReturn(tripDetails);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL)
                        .param("tripperId", String.valueOf(tripperId))
                        .param("tripId", String.valueOf(tripId))
                        .param("size", String.valueOf(size))
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.trips").isNotEmpty())
                .andExpect(jsonPath("$.trips.[*].tripId").exists())
                .andExpect(jsonPath("$.trips.[*].tripperId").exists())
                .andExpect(jsonPath("$.trips.[*].title").exists())
                .andExpect(jsonPath("$.trips.[*].status").exists())
                .andExpect(jsonPath("$.trips.[*].startDate").exists())
                .andExpect(jsonPath("$.trips.[*].endDate").exists())
                .andExpect(jsonPath("$.trips.[*].imageURL").exists())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        queryParameters(
                                parameterWithName("tripperId").description("여행자 ID"),
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
