package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.presentation.trip.TripPeriodUpdateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPeriodUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
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

/**
 * 여행 기간수정을 담당하는 Controller({@link TripPeriodUpdateController})의 문서화 테스트 코드 클래스입니다.
 * @see TripPeriodUpdateController
 */
@WebMvcTest(TripPeriodUpdateController.class)
@DisplayName("여행 기간 수정 API DOCS 테스트")
public class TripPeriodUpdateControllerDocsTest extends RestDocsTestSupport {

    /**
     * TripPeriodUpdateController의 의존성
     */
    @MockBean
    private TripPeriodUpdateService tripPeriodUpdateService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final String ACCESS_TOKEN = "Bearer accessToken";


    /**
     * <p>여행 기간수정 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증하고, 해당 API를 문서화합니다.</p>
     * <ul>
     *     <li>기간 수정이 성공됐다는 응답이 와야합니다. (200 OK, 본문 있음)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자 요청 -> 성공")
    public void updateTripPeriod_with_authorizedUser() throws Exception {
        // given
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);

        Long tripId = 1L;
        LocalDate startDate = LocalDate.of(2023, 4, 1);
        LocalDate endDate = LocalDate.of(2023, 4, 5);

        var request = new TripPeriodUpdateRequest(startDate, endDate);
        var command = TripPeriodUpdateCommand.of(tripId, tripperId, startDate, endDate);

        // when
        ResultActions resultActions = runTest(tripId, createJson(request)); // 정상적으로 인증된 사용자가 요청했을 때

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId)); // 응답 메시지 검증

        verify(tripPeriodUpdateService, times(1)).updateTripPeriod(eq(command)); // 내부 의존성 호출 횟수 검증

        // 문서화
        resultActions
                .andDo(restDocs.document(
                        // 헤더 문서화
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        // 요청 경로변수 문서화
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("기간 수정할 여행 ID")
                        ),
                        // 요청 필드 문서화
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
                        // 응답 필드 문서화
                        responseFields(
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("기간 수정된 여행 식별자(id)")
                        )
                ));
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object tripId, String content) throws Exception {
        return mockMvc.perform(put("/api/trips/{tripId}/period", tripId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }
}
