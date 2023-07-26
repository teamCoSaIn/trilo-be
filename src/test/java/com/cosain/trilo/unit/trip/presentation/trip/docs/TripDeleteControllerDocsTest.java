package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_delete.TripDeleteService;
import com.cosain.trilo.trip.presentation.trip.TripDeleteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 삭제를 담당하는 Controller({@link TripDeleteController})의 문서화 테스트 코드 클래스입니다.
 * @see TripDeleteController
 */
@WebMvcTest(TripDeleteController.class)
@DisplayName("여행 삭제 API DOCS 테스트")
public class TripDeleteControllerDocsTest extends RestDocsTestSupport {

    /**
     * TripDeleteController의 의존성
     */
    @MockBean
    private TripDeleteService tripDeleteService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final String ACCESS_TOKEN = "Bearer accessToken";

    /**
     * <p>여행 삭제 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증하고, 해당 API를 문서화합니다.</p>
     * <ul>
     *     <li>컨텐츠가 없다는 응답이 와야합니다. 이때 본문은 비어있습니다. (204 No Content)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 여행 삭제 요청 -> 성공")
    void tripDeleteDocTest() throws Exception {
        // given
        long requestTripperId = 2L;
        mockingForLoginUserAnnotation(requestTripperId);
        long tripId = 1L;

        // when
        ResultActions resultActions = runTest(tripId); // 인증된 사용자의 여행 삭제 요청

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist()); // 응답 메시지 검증

        verify(tripDeleteService, times(1)).deleteTrip(eq(tripId), eq(requestTripperId)); // 내부 의존성 호출 검증

        // 문서화
        resultActions
                .andDo(restDocs.document(
                        // 요청 헤더 문서화
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        // 경로 변수 문서화
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("삭제할 여행 식별자(id)")
                        )
                ));
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param tripId : 삭제할 여행 식별자(id)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object tripId) throws Exception {
        return mockMvc.perform(delete("/api/trips/{tripId}", tripId)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }
}
