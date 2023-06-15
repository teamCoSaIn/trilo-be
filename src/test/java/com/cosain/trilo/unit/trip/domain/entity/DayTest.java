package com.cosain.trilo.unit.trip.domain.entity;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[TripCommand] Day 테스트")
public class DayTest {

    @ParameterizedTest
    @ValueSource(strings = {"RED", "ORANGE", "PURPLE"})
    @DisplayName("Day의 색상 변경 테스트")
    public void changeColorTest(String colorName) {
        Trip trip = TripFixture.DECIDED_TRIP.createDecided(1L, 1L, "제목", LocalDate.of(2023,5,1), LocalDate.of(2023,5,1));
        Day day = trip.getDays().get(0);

        DayColor color = DayColor.of(colorName);
        day.changeColor(color);

        assertThat(color.name()).isEqualTo(colorName);
    }

    @Nested
    @DisplayName("isIn 메서드 테스트")
    class IsInTest {

        @Test
        @DisplayName("Day의 날짜가 Period에 포함되면 true를 반환한다.")
        public void when_period_contains_date_day_is_in_this_period() {
            // given
            Trip trip = TripFixture.DECIDED_TRIP.createDecided(1L, 1L, "제목", LocalDate.of(2023,5,2), LocalDate.of(2023,5,2));
            Day day = trip.getDays().get(0);

            TripPeriod period = TripPeriod.of(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 3));

            // when & then
            assertTrue(day.isIn(period));
        }

        @Test
        @DisplayName("Day의 날짜가 Period에 포함되지 않으면 false를 반환한다.")
        public void when_period_not_contains_date_day_is_not_in_this_period() {
            // given
            Trip trip = TripFixture.DECIDED_TRIP.createDecided(1L, 1L, "제목", LocalDate.of(2023,5,4), LocalDate.of(2023,5,4));
            Day day = trip.getDays().get(0);

            TripPeriod period = TripPeriod.of(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 3));

            // when & then
            assertFalse(day.isIn(period));
        }

        @Test
        @DisplayName("Period가 빈 기간이면 false를 반환한다.")
        public void when_period_is_empty_then_day_is_not_in_this_period() {
            // given
            Trip trip = TripFixture.DECIDED_TRIP.createDecided(1L, 1L, "제목", LocalDate.of(2023,5,4), LocalDate.of(2023,5,4));
            Day day = trip.getDays().get(0);
            TripPeriod period = TripPeriod.empty();

            // when & then
            assertFalse(day.isIn(period));
        }
    }
}
