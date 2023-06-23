package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleTimeException;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ScheduleTime(일정 시간) 테스트")
public class ScheduleTimeTest {

    @DisplayName("둘다 null 이 아닌 경우 -> 정상 생성")
    @Test
    public void createSuccess() {
        // given
        LocalTime startTime = LocalTime.of(0,0);
        LocalTime endTime = LocalTime.of(0,10);

        // when
        ScheduleTime scheduleTime = ScheduleTime.of(startTime, endTime);

        // then
        assertThat(scheduleTime.getStartTime()).isEqualTo(startTime);
        assertThat(scheduleTime.getEndTime()).isEqualTo(endTime);
    }

    @DisplayName("시작시간 null -> InvalidScheduleTimeException 예외 발생")
    @Test
    public void testStartTimeNull() {
        // given
        LocalTime startTime = null;
        LocalTime endTime = LocalTime.of(0,10);

        // when, then
        assertThatThrownBy(() -> ScheduleTime.of(startTime, endTime))
                .isInstanceOf(InvalidScheduleTimeException.class);
    }

    @DisplayName("종료시간 null -> InvalidScheduleTimeException 예외 발생")
    @Test
    public void testEndTimeNull() {
        // given
        LocalTime startTime = LocalTime.of(0,5);
        LocalTime endTime = null;

        // when, then
        assertThatThrownBy(() -> ScheduleTime.of(startTime, endTime))
                .isInstanceOf(InvalidScheduleTimeException.class);
    }

    @DisplayName("시작, 종료 시간 null -> InvalidScheduleTimeException 예외 발생")
    @Test
    public void testBothTimeNull() {
        // given
        LocalTime startTime = null;
        LocalTime endTime = null;

        // when, then
        assertThatThrownBy(() -> ScheduleTime.of(startTime, endTime))
                .isInstanceOf(InvalidScheduleTimeException.class);
    }

    @DisplayName("디폴트 시간은 0시 0분 부터 0시 0분까지다.")
    @Test
    public void defaultTimeTest() {
        ScheduleTime scheduleTime = ScheduleTime.defaultTime();

        assertThat(scheduleTime).isEqualTo(ScheduleTime.of(LocalTime.of(0, 0), LocalTime.of(0 ,0)));
    }

    @DisplayName("시작시간과 종료 시간이 같으면 동등하다")
    @Test
    public void equalsTest() {
        // given
        LocalTime startTime1 = LocalTime.of(0,0);
        LocalTime endTime1 = LocalTime.of(0,10);

        LocalTime startTime2 = LocalTime.of(0,0);
        LocalTime endTime2 = LocalTime.of(0,10);

        // when
        ScheduleTime scheduleTime1 = ScheduleTime.of(startTime1, endTime1);
        ScheduleTime scheduleTime2 = ScheduleTime.of(startTime2, endTime2);

        // then
        assertThat(scheduleTime1).isEqualTo(scheduleTime2);
    }

}
