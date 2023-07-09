package com.cosain.trilo.unit.trip.presentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.command.service.TripDeleteService;
import com.cosain.trilo.trip.presentation.trip.command.TripDeleteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripDeleteController.class)
@DisplayName("여행 삭제 API DOCS 테스트")
public class TripDeleteControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripDeleteService tripDeleteService;

    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 여행 삭제 요청 -> 성공")
    void tripDeleteDocTest() throws Exception {
        Long tripId = 1L;
        mockingForLoginUserAnnotation();
        willDoNothing().given(tripDeleteService).deleteTrip(eq(tripId), any());

        mockMvc.perform(delete("/api/trips/{tripId}", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("삭제할 여행 식별자(id)")
                        )
                ));


        verify(tripDeleteService).deleteTrip(eq(tripId), any());
    }
}
