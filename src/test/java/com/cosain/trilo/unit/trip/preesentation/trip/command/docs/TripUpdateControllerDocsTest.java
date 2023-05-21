package com.cosain.trilo.unit.trip.preesentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.command.application.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.presentation.trip.command.TripUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.data.redis.connection.DataType.STRING;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripUpdateController.class)
public class TripUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripUpdateUseCase tripUpdateUseCase;

    private final String BASE_URL = "/api/trips";
    private final String ACCESS_TOKEN = "Bearer accessToken";
    @Test
    void 여행_수정_요청() throws Exception{
        mockingForLoginUserAnnotation();
        TripUpdateRequest tripUpdateRequest = new TripUpdateRequest("제목", LocalDate.of(2023, 04, 04), LocalDate.of(2023, 04, 06));
        mockMvc.perform(put(BASE_URL + "/{tripId}", 1)
                .header(HttpHeaders.AUTHORIZATION,ACCESS_TOKEN)
                .content(createJson(tripUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                   requestHeaders(
                           headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 타입 AccessToken")
                   ),
                   pathParameters(
                           parameterWithName("tripId").description("수정할 여행 ID")
                   ),
                   requestFields(
                           fieldWithPath("title").type(STRING).description("여행 제목"),
                           fieldWithPath("startDate").type(STRING).description("여행 시작 일자 (yyyy-MM-dd) "),
                           fieldWithPath("endDate").type(STRING).description("여행 종료 일자 (yyyy-MM-dd)")
                   ),
                   responseFields(
                           fieldWithPath("updatedTripId").type(STRING).description("수정된 여행 ID")
                   )
                ));
    }
}
