package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetailSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.presentation.trip.SingleTripQueryController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SingleTripQueryController.class)
public class SingleTripQueryControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripDetailSearchService tripDetailSearchService;

    private final String BASE_URL = "/api/trips";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void 여행_단건_조회() throws Exception{
        // given
        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        TripDetail tripDetail = new TripDetail(tripId, 2L, "여행 제목", TripStatus.DECIDED, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 5));
        given(tripDetailSearchService.searchTripDetail(anyLong())).willReturn(tripDetail);

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{tripId}", tripId)
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
                                parameterWithName("tripId").description("조회할 여행 ID")
                        ),
                        responseFields(
                                fieldWithPath("tripId").type(NUMBER).description("여행 ID"),
                                fieldWithPath("tripperId").type(NUMBER).description("여행자 ID"),
                                fieldWithPath("title").type(STRING).description("여행 제목"),
                                fieldWithPath("status").type(STRING).description("여행 상태"),
                                fieldWithPath("startDate").type(STRING).description("여행 시작 날짜"),
                                fieldWithPath("endDate").type(STRING).description("여행 끝 날짜")
                        )
                ));

    }
}
