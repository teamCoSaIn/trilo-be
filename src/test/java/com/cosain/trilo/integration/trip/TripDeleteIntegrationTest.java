package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 삭제 기능에 대한 통합 테스트 클래스입니다.
 */
@DisplayName("[통합] 여행 삭제 API 테스트")
class TripDeleteIntegrationTest extends IntegrationTest {

    /**
     * 테스트에서 여행의 삭제 여부를 확인하기 위한 리포지토리 의존성
     */
    @Autowired
    private TripRepository tripRepository;

    /**
     * 테스트에서 일정의 삭제 여부를 확인하기 위한 리포지토리 의존성
     */
    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 테스트에서 공통적으로 사용되는 요청 사용자
     */
    private User requestUser;

    /**
     * 테스트에서 공통적으로 사용되는 여행
     */
    private Trip trip;

    /**
     * 테스트에서 공통적으로 사용되는 Day들
     */
    private Day day1, day2, day3;

    /**
     * 테스트에서 공통적으로 사용되는 일정들
     */
    private Schedule tempSchedule, day1Schedule, day2Schedule, day3Schedule;

    /**
     * 테스트들에서 공통적으로 사용하는 셋업
     */
    @BeforeEach
    void setUp() {
        // common given
        // user는 03.01 ~ 03.03 여행을 계획하고, Day 및 임시보관함에는 각각 일정이 존재함
        requestUser = setupMockGoogleUser();

        trip = setupDecidedTrip(requestUser.getId(), LocalDate.of(2023,3,1), LocalDate.of(2023,3,3));
        day1 = trip.getDays().get(0);
        day2 = trip.getDays().get(1);
        day3 = trip.getDays().get(2);

        tempSchedule = setupTemporarySchedule(trip, 0L);
        day1Schedule = setupDaySchedule(trip, day1, 0L);
        day2Schedule = setupDaySchedule(trip, day2, 0L);
        day3Schedule = setupDaySchedule(trip, day3, 0L);
        flushAndClear();
    }

    /**
     * <p>여행 삭제 요청을 했을 때, 컨트롤러 내부적으로 의도한 대로 동작하는 지 검증합니다.</p>
     * <ul>
     *     <li>컨텐츠가 없다는 응답이 와야합니다. 이때 본문은 비어있습니다. (204 No Content)</li>
     *     <li>실제 여행 및, 여행에 소속된 Day, 일정들이 모두 삭제됨을 검증해야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("인증된 사용자의 올바른 여행 삭제 요청 -> 성공")
    public void deleteTripSuccessTest() throws Exception {
        // given : setup 참고

        // when :
        ResultActions resultActions = runTest(trip.getId(), requestUser); // 인증된 사용자의 올바른 여행 삭제 요청
        flushAndClear();

        // then

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        // 여행이 존재하지 않음
        assertThat(tripRepository.findById(trip.getId())).isEmpty();

        // 해당 여행의 Day가 존재하지 않음
        assertThat(findAllDayByIds(List.of(day1.getId(), day2.getId(), day3.getId()))).isEmpty();

        // 해당 여행의 Schedule이 존재하지 않음
        assertThat(findAllScheduleByIds(
                List.of(tempSchedule.getId(), day1Schedule.getId(), day2Schedule.getId(), day3Schedule.getId()))).isEmpty();
    }

    /**
     * <p>토큰이 없는 사용자가 여행 삭제 요청을 했을 때, 액세스 토큰이 누락됐다는 에러가 발생함을 검증합니다.</p>
     * <ul>
     *     <li>컨텐츠가 없다는 응답이 와야합니다. 이때 본문은 비어있습니다. (204 No Content)</li>
     *     <li>실제 여행 및, 여행에 소속된 Day, 일정들이 모두 삭제됨을 검증해야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void deleteTrip_with_unauthorizedUser() throws Exception {
        // given : setup 참고

        // when : 미인증 사용자의 요청
        ResultActions resultActions = runTestWithoutAuthorization(trip.getId());
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();

        // 응답 메시지 검증(예외)
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 여행은 여전히 존재함
        assertThat(findTrip.getId()).isEqualTo(trip.getId());

        // Day들도 여전히 존재함
        assertThat(findDays.size()).isEqualTo(3);

        // Schedule 들도 여전히 존재함
        assertThat(scheduleRepository.findTripScheduleCount(findTrip.getId())).isEqualTo(4);
    }

    /**
     * <p>경로변수로, 숫자가 아닌 여행 식별자 전달 시, 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (400 Bad Request, 경로 변수 관련 에러)</li>
     *     <li>여행, Day들, 일정들이 그대로 저장되어 있어야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("tripId으로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void deleteTrip_with_stringTripId() throws Exception {
        // given : setup 참고

        // when
        ResultActions resultActions = runTest("가가가", requestUser); // tripId 자리에 숫자가 아닌 문자열이 주입됨
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 여행은 여전히 존재함
        assertThat(findTrip).isNotNull();
        assertThat(findTrip.getId()).isEqualTo(trip.getId());

        // Day들도 여전히 존재함
        assertThat(findDays.size()).isEqualTo(3);

        // 일정들도 여전히 존재함
        assertThat(scheduleRepository.findTripScheduleCount(findTrip.getId())).isEqualTo(4);
    }

    /**
     * <p>존재하지 않는 여행삭제 요청을 했을 때 404 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (404 Not Found, 여행 없음)</li>
     *     <li>여행, Day들, 일정들이 그대로 저장되어 있어야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("존재하지 않는 여행 삭제 요청 -> 예외 발생")
    public void tripNotFound() throws Exception {
        // given : setup + notExistTripId
        Long notExistTripId = trip.getId() + 1L;

        // when : 존재하지 않는 여행 삭제 요청
        ResultActions resultActions = mockMvc.perform(delete("/api/trips/{tripId}", notExistTripId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("trip-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    /**
     * <p>권한이 없는 사용자가 여행삭제 요청을 했을 때 403 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>에러 응답이 와야합니다. (403 Forbidden, 삭제 권한 없음)</li>
     *     <li>여행, Day들, 일정들이 그대로 저장되어 있어야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("다른 사람이 여행 삭제 요청 -> 예외 발생")
    public void noAuthorityTripper() throws Exception {
        // given : setup 참고
        User otherUser = setupMockKakaoUser(); // 다른 사용자

        // when
        ResultActions resultActions = runTest(trip.getId(), otherUser); // 다른 사용자의 요청
        flushAndClear();

        // then
        Trip findTrip = tripRepository.findByIdWithDays(trip.getId()).orElseThrow(IllegalStateException::new);
        List<Day> findDays = findTrip.getDays();

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("trip-0007"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // 여행은 여전히 존재함
        assertThat(findTrip).isNotNull();
        assertThat(findTrip.getId()).isEqualTo(trip.getId());

        // Day들도 여전히 존재함
        assertThat(findDays.size()).isEqualTo(3);

        // 일정들도 여전히 존재함
        assertThat(scheduleRepository.findTripScheduleCount(findTrip.getId())).isEqualTo(4);
    }

    /**
     * 전달된 id들에 해당하는 Day들을 모두 찾아 반환합니다. (테스트용)
     * @param dayIds Day id들
     * @return Day들
     */
    private List<Day> findAllDayByIds(List<Long> dayIds) {
        return em.createQuery("""
                                SELECT d
                                FROM Day as d
                                WHERE d in :dayIds
                                """, Day.class)
                .setParameter("dayIds", dayIds)
                .getResultList();
    }

    /**
     * 전달된 id들에 해당하는 일정들을 모두 찾아 반환합니다. (데스트용)
     * @param scheduleIds : 일정 id들
     * @return 일정들
     */
    private List<Schedule> findAllScheduleByIds(List<Long> scheduleIds) {
        return em.createQuery("""
                        SELECT s
                        FROM Schedule s
                        WHERE s.id in :scheduleIds
                        """, Schedule.class)
                .setParameter("scheduleIds", scheduleIds)
                .getResultList();
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param tripId 삭제할 여행 id(식별자)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(Object tripId, User requestUser) throws Exception {
        return mockMvc.perform(delete("/api/trips/{tripId}", tripId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    /**
     * 미인증 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param tripId 삭제할 여행 id(식별자)
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTestWithoutAuthorization(Object tripId) throws Exception {
        return mockMvc.perform(delete("/api/trips/{tripId}", tripId)
                // 인증헤더 없음
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
