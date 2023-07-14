package com.cosain.trilo.integration.trip;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import com.cosain.trilo.trip.presentation.schedule.dto.request.ScheduleUpdateRequest;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[통합] 일정 수정 API 테스트")
class ScheduleUpdateIntegrationTest extends IntegrationTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("인증된 사용자 올바른 요청 -> 일정 수정됨")
    public void updateSchedule_with_authorizedUser() throws Exception {
        // given ======================================================================================

        User user = setupMockNaverUser();
        Schedule beforeSchedule = setupMockSchedule(user.getId());

        String rawTitle = "수정할 제목";
        String rawContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 5);

        var request = new ScheduleUpdateRequest(rawTitle, rawContent, startTime, endTime);
        flushAndClear();

        // when : 정상 요청 =============================================================================
        ResultActions resultActions = runTest(beforeSchedule.getId(), createRequestJson(request), user);
        flushAndClear();

        // then ========================================================================================

        // then1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(beforeSchedule.getId()));

        // then2 : 실제 Schedule 필드 검증
        Schedule retrievedSchedule = retrieveSchedule(beforeSchedule.getId());
        assertThat(retrievedSchedule.getScheduleTitle()).isEqualTo(ScheduleTitle.of(rawTitle));
        assertThat(retrievedSchedule.getScheduleContent()).isEqualTo(ScheduleContent.of(rawContent));
        assertThat(retrievedSchedule.getScheduleTime()).isEqualTo(ScheduleTime.of(startTime, endTime));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 예외 발생")
    public void testUnAuthorizedUser() throws Exception {
        // given
        User user = setupMockNaverUser();
        Schedule beforeSchedule = setupMockSchedule(user.getId());

        String rawTitle = "수정할 제목";
        String rawContent = "수정할 내용";
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 5);

        var request = new ScheduleUpdateRequest(rawTitle, rawContent, startTime, endTime);
        flushAndClear();

        // when : 미인증 사용자 ==========================================================================
        ResultActions resultActions = runTestWithUnAuthorization(beforeSchedule.getId(), createRequestJson(request));
        flushAndClear();

        // then ========================================================================================

        // then1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());


        // then2 : 일정에는 변화 없음 검증
        Schedule retrievedSchedule = retrieveSchedule(beforeSchedule.getId());

        assertThat(retrievedSchedule.getScheduleTitle()).isEqualTo(beforeSchedule.getScheduleTitle());
        assertThat(retrievedSchedule.getScheduleContent()).isEqualTo(beforeSchedule.getScheduleContent());
        assertThat(retrievedSchedule.getScheduleTime()).isEqualTo(beforeSchedule.getScheduleTime());
    }

    private Schedule setupMockSchedule(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);

        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, 0L);
        em.persist(schedule);
        return schedule;
    }

    private ResultActions runTest(Object scheduleId, String content, User tripper) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}", scheduleId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(tripper))
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions runTestWithUnAuthorization(Object scheduleId, String content) throws Exception {
        return mockMvc.perform(put("/api/schedules/{scheduleId}", scheduleId)
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private Schedule retrieveSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(IllegalStateException::new);
    }
}
