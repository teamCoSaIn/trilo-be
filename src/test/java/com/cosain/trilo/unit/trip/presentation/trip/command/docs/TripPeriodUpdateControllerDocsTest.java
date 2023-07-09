package com.cosain.trilo.unit.trip.presentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.command.service.TripPeriodUpdateService;
import com.cosain.trilo.trip.application.trip.dto.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.dto.factory.TripPeriodUpdateCommandFactory;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.presentation.trip.command.TripPeriodUpdateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripPeriodUpdateRequest;
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
import static org.mockito.Mockito.times;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripPeriodUpdateController.class)
@DisplayName("여행 기간 수정 API DOCS 테스트")
public class TripPeriodUpdateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripPeriodUpdateService tripPeriodUpdateService;

    @MockBean
    private TripPeriodUpdateCommandFactory tripPeriodUpdateCommandFactory;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripPeriod_with_authorizedUser() throws Exception {
        // given
        mockingForLoginUserAnnotation();

        Long tripId = 1L;
        LocalDate startDate = LocalDate.of(2023, 4, 1);
        LocalDate endDate = LocalDate.of(2023, 4, 5);

        TripPeriodUpdateRequest request = new TripPeriodUpdateRequest(startDate, endDate);
        TripPeriodUpdateCommand command = new TripPeriodUpdateCommand(TripPeriod.of(startDate, endDate));

        given(tripPeriodUpdateCommandFactory.createCommand(eq(startDate), eq(endDate))).willReturn(command);
        willDoNothing().given(tripPeriodUpdateService).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));

        mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("기간 수정할 여행 ID")
                        ),
                        requestFields(
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
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("기간 수정된 여행 식별자(id)")
                        )
                ));

        verify(tripPeriodUpdateService, times(1)).updateTripPeriod(eq(tripId), any(), any(TripPeriodUpdateCommand.class));
        verify(tripPeriodUpdateCommandFactory, times(1)).createCommand(eq(startDate), eq(endDate));
    }
}
