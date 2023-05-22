package com.cosain.trilo.unit.trip.domain.entity.vo;

import com.cosain.trilo.trip.domain.exception.InvalidPeriodException;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

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

    @Nested
    @DisplayName("TripPeriod.empty() 를 통해 반환 된 TripPeriod는")
    class EmptyTripPeriodTest {

        // given
        TripPeriod emptyPeriod = TripPeriod.empty();

        @Test
        @DisplayName("시작일이 null이다.")
        public void emptyTripPeriodStartDate() {
            // when & then
            assertThat(emptyPeriod.getStartDate()).isNull();
        }

        @Test
        @DisplayName("종료일이 null이다.")
        public void emptyPeriodEndDate() {
            // when & then
            assertThat(emptyPeriod.getEndDate()).isNull();
        }
    }

    @Nested
    @DisplayName("Intersection 테스트")
    class IntersectionTest {

        @Nested
        @DisplayName("겹치지 않는 기간 테스트")
        class NotOverlappedPeriod {
            @Test
            @DisplayName("[비어있는 기간] intersection [비어있는 기간] -> [비어있는 기간]")
            public void emptyPeriod_and_emptyPeriod_intersection() {
                // given
                TripPeriod period = TripPeriod.empty();
                TripPeriod other = TripPeriod.empty();

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isEqualTo(TripPeriod.empty());
            }

            @Test
            @DisplayName("[비어있는 기간] intersection [비어있지 않은 기간] -> [비어있는 기간]")
            public void emptyPeriod_and_not_emptyPeriod_intersection() {
                // given
                TripPeriod period = TripPeriod.empty();
                TripPeriod other = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5));

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isEqualTo(TripPeriod.empty());
            }

            @Test
            @DisplayName("[비어있지 않은 기간] intersection [비어 있는 기간] -> [비어있는 기간]")
            public void not_emptyPeriod_and_emptyPeriod_intersection() {
                // given
                TripPeriod period = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5));
                TripPeriod other = TripPeriod.empty();

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isEqualTo(TripPeriod.empty());
            }

            @Test
            @DisplayName("[앞선 기간] intersection [늦은 기간] -> [비어있는 기간]")
            public void beforePeriod_and_AfterPeriod_intersection() {
                // given
                TripPeriod beforePeriod = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5));
                TripPeriod afterPeriod = TripPeriod.of(LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 10));

                // when
                TripPeriod intersection = beforePeriod.intersection(afterPeriod);

                // then
                assertThat(intersection).isEqualTo(TripPeriod.empty());
            }

            @Test
            @DisplayName("[늦은 기간] intersection [앞선 기간] -> [비어있는 기간]")
            public void afterPeriod_and_beforePeriod_intersection() {
                // given
                TripPeriod afterPeriod = TripPeriod.of(LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 10));
                TripPeriod beforePeriod = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5));

                // when
                TripPeriod intersection = afterPeriod.intersection(beforePeriod);

                // then
                assertThat(intersection).isEqualTo(TripPeriod.empty());
            }
        }

        @Nested
        @DisplayName("겹치는 기간 테스트")
        class OverlappedPeriod {
            @Test
            @DisplayName("뒤에서 겹치는 경우 -> 뒤에서 겹치는 구간")
            public void back_overlapped_test() {
                // given
                TripPeriod period = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 4));
                TripPeriod other = TripPeriod.of(LocalDate.of(2023, 3, 3), LocalDate.of(2023, 3, 6));

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isNotEqualTo(TripPeriod.empty());
                assertThat(intersection.getStartDate()).isEqualTo(LocalDate.of(2023, 3, 3));
                assertThat(intersection.getEndDate()).isEqualTo(LocalDate.of(2023, 3, 4));
            }

            @Test
            @DisplayName("앞에서 겹치는 경우 -> 앞에서 겹치는 구간")
            public void front_overlapped_test() {
                // given
                TripPeriod period = TripPeriod.of(LocalDate.of(2023, 3, 3), LocalDate.of(2023, 3, 6));
                TripPeriod other = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 4));

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isNotEqualTo(TripPeriod.empty());
                assertThat(intersection.getStartDate()).isEqualTo(LocalDate.of(2023, 3, 3));
                assertThat(intersection.getEndDate()).isEqualTo(LocalDate.of(2023, 3, 4));
            }

            @Test
            @DisplayName("[큰 기간] intersection [내부에 포함된 기간] -> [내부에 포함된 기간]")
            public void outerPeriod_and_innerPeriod_intersection() {
                // given
                TripPeriod period = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 7));
                TripPeriod other = TripPeriod.of(LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 4));

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isNotEqualTo(TripPeriod.empty());
                assertThat(intersection.getStartDate()).isEqualTo(LocalDate.of(2023, 3, 2));
                assertThat(intersection.getEndDate()).isEqualTo(LocalDate.of(2023, 3, 4));
            }

            @Test
            @DisplayName("[내부에 포함된 기간] intersection [큰 기간] -> [내부에 포함된 기간]")
            public void innerPeriod_and_outerPeriod_intersection() {
                // given
                TripPeriod period = TripPeriod.of(LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 4));
                TripPeriod other = TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 7));

                // when
                TripPeriod intersection = period.intersection(other);

                // then
                assertThat(intersection).isNotEqualTo(TripPeriod.empty());
                assertThat(intersection.getStartDate()).isEqualTo(LocalDate.of(2023, 3, 2));
                assertThat(intersection.getEndDate()).isEqualTo(LocalDate.of(2023, 3, 4));
            }
        }

    }

    @Nested
    @DisplayName("contains 메서드 테스트")
    class ContainsTest {

        @Nested
        @DisplayName("비어있는 TripPeriod일 때")
        class When_Period_isEmpty {

            @ParameterizedTest
            @DisplayName("false 반환")
            @ValueSource(strings = {"1970-01-01", "2022-12-19", "2023-04-02", "2023-04-03", "2024-03-17", "2033-07-18"})
            public void it_returns_false(String dateString) {
                //given
                TripPeriod period = TripPeriod.empty();
                LocalDate date = LocalDate.parse(dateString);

                // when & then
                assertThat(period.contains(date)).isFalse();
            }
        }

        @Nested
        @DisplayName("비어있지 않은 TripPeriod일 때")
        class When_Period_isNotEmpty {
            // common given
            TripPeriod period = TripPeriod.of(LocalDate.of(2023, 4, 2), LocalDate.of(2023, 4, 5));

            @ParameterizedTest
            @DisplayName("지정 기간 이전 날짜의 경우 false 반환")
            @ValueSource(strings = {"1970-01-01", "2022-12-19", "2023-02-28", "2023-04-01"})
            public void before_date_will_return_false(String dateString) {
                LocalDate date = LocalDate.parse(dateString);

                // when & then
                assertThat(period.contains(date)).isFalse();
            }

            @ParameterizedTest
            @DisplayName("지정 기간 사이 날짜의 경우 true 반환")
            @ValueSource(strings = {"2023-04-02", "2023-04-03", "2023-04-04", "2023-04-05"})
            public void between_date_will_return_true(String dateString) {
                LocalDate date = LocalDate.parse(dateString);

                // when & then
                assertThat(period.contains(date)).isTrue();
            }

            @ParameterizedTest
            @DisplayName("지정 기간 이후 날짜의 경우 true 반환")
            @ValueSource(strings = {"2023-04-06", "2023-04-07", "2024-03-17", "2033-07-18"})
            public void after_date_will_return_false(String dateString) {
                LocalDate date = LocalDate.parse(dateString);

                // when & then
                assertThat(period.contains(date)).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("DateStreamTest")
    class DateStreamTest {

        @Test
        @DisplayName("EmptyPeriod의 dateStream은 빈 Stream이다.")
        public void emptyPeriod_create_emptyStream() {
            // given
            TripPeriod tripPeriod = TripPeriod.empty();

            // when
            Stream<LocalDate> dateStream = tripPeriod.dateStream();

            // then
            List<LocalDate> dates = dateStream.toList();
            assertThat(dates).isEmpty();
        }

        @Test
        @DisplayName("비어있지 않은 기간의 dateStream은 시작일부터 종료일까지 날짜들의 Stream이다.")
        public void notEmptyPeriod_create_dateStream() {
            // given
            TripPeriod tripPeriod = TripPeriod.of(LocalDate.of(2023,1,1), LocalDate.of(2023,1,5));

            // when
            Stream<LocalDate> dateStream = tripPeriod.dateStream();

            // then
            List<LocalDate> dates = dateStream.toList();
            assertThat(dates).containsExactly(
                    LocalDate.of(2023,1,1), LocalDate.of(2023,1,2),
                    LocalDate.of(2023,1,3), LocalDate.of(2023,1,4),
                    LocalDate.of(2023,1,5));
        }
    }
}
