package com.cosain.trilo.integration.trip;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.command.dto.request.TripTitleUpdateRequest;
import com.cosain.trilo.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("[통합] 여행 제목 수정 API 테스트")
public class TripTitleUpdateIntegrationTest extends IntegrationTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("여행 제목 수정 -> 여행 제목 수정 됨")
    @Test
    void updateTitleTest() throws Exception {
        // given
        User user = setupMockKakaoUser();
        Trip trip = setupMockTrip("여행 제목", user.getId());
        log.info("beforeTrip = {}", trip);

        String newTitle = "수정 제목";
        TripTitleUpdateRequest request = new TripTitleUpdateRequest(newTitle);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/trips/{tripId}/title", trip.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        em.flush();
        em.clear();

        // then
        Trip findTrip = tripRepository.findById(trip.getId()).orElse(null);
        log.info("findTrip = {}", findTrip);

        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(trip.getId()));


        assertThat(findTrip).isNotNull();
        assertThat(findTrip.getTripTitle()).isEqualTo(TripTitle.of(newTitle));
    }

    private Trip setupMockTrip(String rawTitle, Long tripperId) {
        Trip trip = TripFixture.undecided_nullId_Title(tripperId, rawTitle);
        tripRepository.save(trip);
        return trip;
    }
}
