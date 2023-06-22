package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduleTime(일정 시간) 테스트")
public class ScheduleTimeTest {

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
