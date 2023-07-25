package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateService;
import com.cosain.trilo.trip.presentation.trip.TripCreateController;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 생성을 담당하는 Controller({@link TripCreateController})의 문서화 테스트 코드 클래스입니다.
 * @see TripCreateController
 */
@WebMvcTest(TripCreateController.class)
@DisplayName("여행 생성 API DOCS 테스트")
public class TripCreateControllerDocsTest extends RestDocsTestSupport {

    /**
     * TripCreateController의 의존성
     */
    @MockBean
    private TripCreateService tripCreateService;

    /**
     * 테스트에서 사용할 가짜 Authorization Header 값
     */
    private final String ACCESS_TOKEN = "Bearer accessToken";


    /**
     * <p>여행 생성 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증하고, 해당 API를 문서화합니다.</p>
     * <ul>
     *     <li>생성이 성공됐다는 응답이 와야합니다. (201 Created, 생성된 사용자 식별자)</li>
     *     <li>내부 의존성이 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 여행 생성 요청 -> 성공")
    void successTest() throws Exception {
        // given
        Long tripperId= 1L;
        mockingForLoginUserAnnotation(tripperId); // 인증된 사용자 mocking 됨

        String rawTitle = "제목";
        var request = new TripCreateRequest(rawTitle);

        Long createdTripId = 1L;
        var command = TripCreateCommand.of(tripperId, rawTitle);
        given(tripCreateService.createTrip(eq(command))).willReturn(createdTripId); // 여행 생성 후 서비스에서 반환받을 여행 식별자 mocking

        // when
        ResultActions resultActions = runTest(createJson(request)); // 정상적으로 인증된 사용자가 요청했을 때

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value(createdTripId)); // 상태코드 및 응답 필드 검증

        verify(tripCreateService, times(1)).createTrip(eq(command)); // 내부 의존성 호출 검증


        // 문서화
        resultActions
                .andDo(restDocs.document(
                        // 헤더 문서화
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        // 요청 필드 문서화
                        requestFields(
                                fieldWithPath("title")
                                        .type(STRING)
                                        .description("여행의 제목")
                                        .attributes(key("constraints").value("null 또는 공백일 수 없으며, 길이는 1-20자까지만 허용됩니다."))
                        ),
                        // 응답 필드 문서화
                        responseFields(
                                fieldWithPath("tripId")
                                        .type(NUMBER)
                                        .description("생성된 여행의 식별자(id)")
                        )
                ));
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(String content) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }
}
