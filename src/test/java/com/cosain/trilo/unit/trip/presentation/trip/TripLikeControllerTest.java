package com.cosain.trilo.unit.trip.presentation.trip;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_like.TripLikeFacade;
import com.cosain.trilo.trip.presentation.trip.TripLikeController;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripLikeController.class)
public class TripLikeControllerTest extends RestControllerTest {

    @MockBean
    private TripLikeFacade tripLikeFacade;

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String BASE_URL = "/api/trips/{tripId}/likes";
    @Nested
    class 좋아요_요청_테스트{

        @Test
        void 인증된_사용자가_요청할_경우_200() throws Exception {

            Long tripId = 1L;
            mockingForLoginUserAnnotation(tripId);
            mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL, tripId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 미인증_사용자가_요청한_경우_401() throws Exception{
            Long tripId = 1L;

            mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL, tripId)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class 좋아요_취소_요청_테스트{
        @Test
        void 인증된_사용자가_요청할_경우_204() throws Exception {

            Long tripId = 1L;
            mockingForLoginUserAnnotation(tripId);

            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL, tripId)
                            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void 미인증_사용자가_요청한_경우_401() throws Exception{
            Long tripId = 1L;

            mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL, tripId)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }




}
