package com.cosain.trilo.unit.trip.presentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.command.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.command.TripUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripUpdateController.class)
@DisplayName("여행 수정 API DOCS 테스트")
public class TripUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripUpdateUseCase tripUpdateUseCase;

    @MockBean
    private TripUpdateCommandFactory tripUpdateCommandFactory;

    private final String BASE_URL = "/api/trips";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 여행 수정 요청 -> 성공")
    void tripUpdateDocTest() throws Exception {

        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        String rawTitle = "변경할 제목";
        LocalDate startDate = LocalDate.of(2023, 4, 4);
        LocalDate endDate = LocalDate.of(2023, 4, 6);
        TripUpdateRequest request = new TripUpdateRequest(rawTitle, startDate, endDate);

        given(tripUpdateCommandFactory.createCommand(eq(rawTitle), eq(startDate), eq(endDate)))
                .willReturn(new TripUpdateCommand(TripTitle.of(rawTitle), TripPeriod.of(startDate, endDate)));
        willDoNothing().given(tripUpdateUseCase).updateTrip(eq(tripId), any(), any(TripUpdateCommand.class));

        // when & then
        mockMvc.perform(put(BASE_URL + "/{tripId}", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("수정할 여행 ID")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(STRING)
                                        .description("여행 제목")
                                        .attributes(key("constraints").value("null 또는 공백일 수 없으며, 길이는 1-20자까지만 허용됩니다.")),
                                fieldWithPath("startDate")
                                        .type(STRING)
                                        .optional()
                                        .description("여행 시작 일자 (형식 : yyyy-MM-dd)")
                                        .attributes(key("constraints").value("startDate,endDate는 한쪽만 null이여선 안 되며(둘다 null은 가능), endDate가 startDate보다 앞서선 안 됩니다. 여행 일수는 최대 10일까지 허용됩니다.")),
                                fieldWithPath("endDate")
                                        .type(STRING)
                                        .optional()
                                        .description("여행 종료 일자 (형식 : yyyy-MM-dd)")
                                        .attributes(key("constraints").value("startDate 참고"))
                        ),
                        responseFields(
                                fieldWithPath("updatedTripId")
                                        .type(NUMBER)
                                        .description("수정된 여행 식별자(id)")
                        )
                ));

        verify(tripUpdateUseCase).updateTrip(eq(tripId), any(), any(TripUpdateCommand.class));
        verify(tripUpdateCommandFactory).createCommand(eq(rawTitle), eq(startDate), eq(endDate));
    }
}
