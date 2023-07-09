package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripImage;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripCreateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("[통합] 여행 생성 API 테스트")
public class TripCreateIntegrationTest extends IntegrationTest {

    @Autowired
    TripRepository tripRepository;

    @DisplayName("여행 생성 -> 여행 생성 됨")
    @Test
    void createTest() throws Exception {
        // given
        User user = setupMockKakaoUser();
        String title = "여행 제목";
        TripCreateRequest createRequest = new TripCreateRequest(title);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/trips")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(createRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        TripCreateResponse response = createResponseObject(resultActions, TripCreateResponse.class);
        Trip createdTrip = tripRepository.findById(response.getTripId()).orElse(null);

        // then 1 - 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").isNotEmpty());

        // then - 2 생성된 Trip의 필드 검증
        assertThat(createdTrip).isNotNull();
        assertThat(createdTrip.getTripperId()).isEqualTo(user.getId());
        assertThat(createdTrip.getTripTitle()).isEqualTo(TripTitle.of(title));
        assertThat(createdTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(createdTrip.getTripImage()).isEqualTo(TripImage.defaultImage());
        assertThat(createdTrip.getStatus()).isSameAs(TripStatus.UNDECIDED);
        assertThat(createdTrip.getDays()).isEmpty();
        assertThat(createdTrip.getTemporaryStorage()).isEmpty();
    }

}
