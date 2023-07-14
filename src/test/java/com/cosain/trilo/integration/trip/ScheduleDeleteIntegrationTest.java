package com.cosain.trilo.integration.trip;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 일정 삭제 API 테스트")
class ScheduleDeleteIntegrationTest extends IntegrationTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("일정 삭제 -> 일정 삭제됨")
    @WithMockUser
    public void successTest() throws Exception {
        // given
        User user = setupMockNaverUser();
        Schedule schedule = setupSchedule(user.getId());
        flushAndClear();

        // when
        ResultActions resultActions = runTest(schedule.getId(), user);
        flushAndClear();

        // then ===============================================================================

        // then1 : 응답 데이터 검증
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        // then2 : 데이터베이스 조회 결과 해당 Schedule 없음
        assertThat(scheduleRepository.findById(schedule.getId())).isEmpty();
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void deleteSchedule_with_unauthorizedUser() throws Exception {
        // given ==============================================================================
        User user = setupMockNaverUser();
        Schedule schedule = setupSchedule(user.getId());
        flushAndClear();

        // when : 미인증 사용자 ===================================================================
        ResultActions resultActions = runTestWithUnAuthorization(schedule.getId());
        flushAndClear();

        // then ==============================================================================

        // then1 : 응답 데이터 검증
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then2: 데이터베이스 조회 결과 해당 Schedule 존재함
        assertThat(retrieveSchedule(schedule.getId())).isNotEmpty();
    }

    private Schedule setupSchedule(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);

        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, 0L);
        em.persist(schedule);
        return schedule;
    }

    private ResultActions runTest(Object scheduleId, User tripper) throws Exception {
        return mockMvc.perform(delete("/api/schedules/{scheduleId}", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(tripper))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions runTestWithUnAuthorization(Object scheduleId) throws Exception {
        return mockMvc.perform(delete("/api/schedules/{scheduleId}", scheduleId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private Optional<Schedule> retrieveSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

}
