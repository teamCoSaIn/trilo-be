package com.cosain.trilo.unit.trip.command.domain.vo;

import com.cosain.trilo.trip.command.domain.exception.InvalidPeriodException;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[TripCommand] TripPeriod 테스트")
public class TripPeriodTest {

    @Nested
    @DisplayName("TripPeriod를 'of' 메서드로 생성할 때")
    class When_Create_With_of {

        @Nested
        @DisplayName("startDate, endDate 모두 null 이면")
        class If_StartDate_and_EndDate_are_null {

            @Test
            @DisplayName("빈 기간에 해당하는 날짜가 반환된다.")
            public void it_returns_empty_period() {
                // given
                LocalDate startDate = null;
                LocalDate endDate = null;

                // when
                TripPeriod period = TripPeriod.of(startDate, endDate);

                // then
                assertThat(period).isEqualTo(TripPeriod.empty());
            }
        }

        @Nested
        @DisplayName("startDate가 null, endDate가 null이 아니면")
        class If_StartDate_is_null_and_EndDate_is_not_null {

            @Test
            @DisplayName("InvalidPeriodException 예외가 발생한다")
            public void it_throws_InvalidPeriodException() {
                // given
                LocalDate startDate = null;
                LocalDate endDate = LocalDate.of(2023, 4, 20);

                // when & then
                assertThatThrownBy(() -> TripPeriod.of(startDate, endDate)).isInstanceOf(InvalidPeriodException.class);
            }
        }

        @Nested
        @DisplayName("startDate가 null이 아니고, endDate가 null 이면")
        class If_StartDate_is_not_null_and_EndDate_is_null {

            @Test
            @DisplayName("InvalidPeriodException 예외가 발생한다")
            public void it_throws_InvalidPeriodException() {
                // given
                LocalDate startDate = LocalDate.of(2023, 4, 20);
                LocalDate endDate = null;

                // when & then
                assertThatThrownBy(() -> TripPeriod.of(startDate, endDate)).isInstanceOf(InvalidPeriodException.class);
            }
        }

        @Nested
        @DisplayName("endDate가 startDate보다 앞서면")
        class If_endDate_is_before_startDate {

            @Test
            @DisplayName("InvalidPeriodException 예외가 발생한다")
            public void it_throws_InvalidPeriodException() {
                // given
                LocalDate startDate = LocalDate.of(2023, 4, 20);
                LocalDate endDate = LocalDate.of(2023, 4, 19);

                // when & then
                assertThatThrownBy(() -> TripPeriod.of(startDate, endDate)).isInstanceOf(InvalidPeriodException.class);
            }
        }

        @Nested
        @DisplayName("endDate가 startDate보다 뒤에 오면")
        class If_endDate_is_after_startDate {

            @Test
            @DisplayName("정상적으로 TripPeriod가 생성된다.")
            public void it_returns_TripPeriod_successfully() {
                // given
                LocalDate startDate = LocalDate.of(2023, 4, 19);
                LocalDate endDate = LocalDate.of(2023, 4, 20);

                // when
                TripPeriod period = TripPeriod.of(startDate, endDate);

                // then
                assertThat(period.getStartDate()).isEqualTo(startDate);
                assertThat(period.getEndDate()).isEqualTo(endDate);
            }
        }

        @Nested
        @DisplayName("startDate와 endDate가 같으면")
        class If_startDate_is_same_as_endDate {

            @Test
            @DisplayName("정상적으로 TripPeriod가 생성된다.")
            public void it_returns_TripPeriod_successfully() {
                // given
                LocalDate startDate = LocalDate.of(2023, 4, 19);
                LocalDate endDate = LocalDate.of(2023, 4, 19);

                // when
                TripPeriod period = TripPeriod.of(startDate, endDate);

                // then
                assertThat(period.getStartDate()).isEqualTo(startDate);
                assertThat(period.getEndDate()).isEqualTo(endDate);
            }
        }
    }

}
