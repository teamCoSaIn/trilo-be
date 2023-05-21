package com.cosain.trilo.unit.trip.preesentation.trip.command.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.command.service.TripCreateService;
import com.cosain.trilo.trip.presentation.trip.command.TripCreateController;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.data.redis.connection.DataType.STRING;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripCreateController.class)
public class TripCreateControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripCreateService tripCreateService;

    private final String BASE_URL = "/api/trips";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    void 여행_생성_요청() throws Exception{

        TripCreateRequest tripCreateRequest = new TripCreateRequest("제목");
        given(tripCreateService.createTrip(any(),any())).willReturn(1L);
        mockingForLoginUserAnnotation();

        mockMvc.perform(post(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(tripCreateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                    requestFields(
                            fieldWithPath("title").type(STRING).description("제목")
                    ),
                    responseFields(
                            fieldWithPath("tripId").type(STRING).description("생성된 여행의 식별자 ID")
                    )
                ));
    }
}
