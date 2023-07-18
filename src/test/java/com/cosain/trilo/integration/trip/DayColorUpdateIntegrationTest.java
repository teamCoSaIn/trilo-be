package com.cosain.trilo.integration.trip;


import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[통합] Day 색상 수정 API 테스트")
public class DayColorUpdateIntegrationTest extends IntegrationTest {

    @Autowired
    private DayRepository dayRepository;

    private User user;
    private Day day;

    @BeforeEach
    void setup() {
        // common given
        user = setupMockKakaoUser();
        day = setupDay(user.getId(), DayColor.BLACK);

        flushAndClear();
    }

    @ParameterizedTest
    @ValueSource(strings = {"RED", "Orange", "light_GREEN", "PURPLE"})
    @DisplayName("인증된 사용자의 DayColor 수정 요청 -> 성공")
    public void successTest(String updateColorName) throws Exception {
        // given : setup
        DayColorUpdateRequest request = new DayColorUpdateRequest(updateColorName);

        // when : 색상 정상 수정
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then 1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(day.getId()));

        // then 2: 실제 색상이 변경됐는 지 테스트
        assertThat(findDay.getDayColor()).isEqualTo(DayColor.of(updateColorName));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    public void updateDayColor_with_unauthorizedUser() throws Exception {
        // given
        String updateColorName = "RED";
        DayColorUpdateRequest request = new DayColorUpdateRequest(updateColorName);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then 1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateDayColor_with_emptyContent() throws Exception {
        // given : setup + content
        String content = "";

        // when : 비어있는 바디 요청
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then 1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createTrip_with_invalidContent() throws Exception {
        // given: setup + content
        String content = """
                {
                    "colorName": 따옴표 안 감싼 색상명
                }
                """;

        // when : 형식이 올바르지 않은 Body
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then1 : 응답메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    @Test
    @DisplayName("DayId로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void updateDayColor_with_notNumberDayId() throws Exception {
        // given: setup + notNumberDayId
        String notNumberDayId = "가가가";
        DayColorUpdateRequest request = new DayColorUpdateRequest("RED");

        // when : dayId 경로변수에 올바르지 않은 타입의 값 주입
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", notNumberDayId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then 2: 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    @Test
    @DisplayName("존재하지 않는 Day의 색상 수정 -> 예외 발생")
    void testDayNotFound() throws Exception {
        // given
        Long notExistDayId = day.getId() + 1L;
        DayColorUpdateRequest request = new DayColorUpdateRequest("BLUE");

        // when : 존재하지 않는 Day Id
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", notExistDayId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("day-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then2 : 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    @Test
    @DisplayName("다른 여행자(사용자가) Day 색상 수정 시도 -> 예외 발생")
    void testNoAuthority() throws Exception {
        // given : setup + otherUser
        User otherUser = setupMockGoogleUser();
        DayColorUpdateRequest request = new DayColorUpdateRequest("BLUE");

        // when : 다른 사용자의 Day 색상 수정 시도
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(otherUser))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then1 : 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("day-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        // then2 : 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    @Test
    @DisplayName("변경하려는 색상 이름이 누락 -> 요청 검증 예외")
    public void nullColorName() throws Exception {
        // given : setup
        DayColorUpdateRequest request = new DayColorUpdateRequest(null);

        // when : 색상을 "RED" 로 수정
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then 1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("day-0003"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        // then2 : 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }


    @ValueSource(strings = {"adfadf", "rainbow", "pink", "", "999", "GOLD"})
    @ParameterizedTest
    @DisplayName("변경하려는 색상 이름이 유효하지 않음 -> 검증예외 발생")
    public void testInvalidDayColorName(String updateColorName) throws Exception  {
        // given : setup
        DayColorUpdateRequest request = new DayColorUpdateRequest(updateColorName);

        // when : 색상 정상 수정
        ResultActions resultActions = mockMvc.perform(put("/api/days/{dayId}/color", day.getId())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(user))
                .content(createRequestJson(request))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        flushAndClear();

        // then
        Day findDay = em.find(Day.class, day.getId());

        // then 1: 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("day-0003"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists());

        // then2 : 실제 색상은 여전히 유지됨
        assertThat(findDay.getDayColor()).isEqualTo(day.getDayColor());
    }

    private Day setupDay(Long tripperId, DayColor dayColor) {
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        Trip trip = TripFixture.decided_nullId_Color(tripperId, startDate, endDate, dayColor);
        Day day = trip.getDays().get(0);

        em.persist(trip);
        em.persist(day);

        return day;
    }
}
