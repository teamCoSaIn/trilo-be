package com.cosain.trilo.unit.trip.presentation.trip.docs;

import com.cosain.trilo.support.RestDocsTestSupport;
import com.cosain.trilo.trip.application.trip.service.trip_like.TripLikeFacade;
import com.cosain.trilo.trip.presentation.trip.TripLikeController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripLikeController.class)
public class TripLikeControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    private TripLikeFacade tripLikeFacade;

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String BASE_URL = "/api/trips/{tripId}/likes";

    @Test
    void 좋아요_등록() throws Exception {

        Long tripId = 1L;
        mockingForLoginUserAnnotation(tripId);

        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL, tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("좋아요할 여행(Trip)의 식별자(id)")
                        )
                ));
    }


    @Test
    void 좋아요_취소() throws Exception {

        Long tripId = 1L;
        mockingForLoginUserAnnotation(tripId);

        mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL, tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer 타입 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("tripId")
                                        .description("좋아요 취소할 여행(Trip)의 식별자(id)")
                        )
                ));
    }


}
