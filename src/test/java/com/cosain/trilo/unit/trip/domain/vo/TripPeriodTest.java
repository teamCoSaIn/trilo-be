package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.common.exception.trip.InvalidPeriodException;
import com.cosain.trilo.common.exception.trip.TooLongPeriodException;
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

/**
 * 여행 기간 VO(값 객체)의 테스트코드 입니다.
 */
@DisplayName("[TripCommand] TripPeriod 테스트")
public class TripPeriodTest {

    /**
     * 여행기간 생성에 대한 테스트들입니다.
     */
    @Nested
    @DisplayName("TripPeriod를 'of' 메서드로 생성할 때")
    class TripPeriod_Create_Test {

        @Test
        @DisplayName("시작일, 종료일 모두 Null -> 성공(비어있는 기간)")
        void emptyPeriod() {
            // given
            LocalDate startDate = null;
            LocalDate endDate = null;

            // when
            TripPeriod period = TripPeriod.of(startDate, endDate);

            // then
            assertThat(period).isEqualTo(TripPeriod.empty());
            assertThat(period.getStartDate()).isNull();
            assertThat(period.getEndDate()).isNull();
        }

        @Test
        @DisplayName("startDate와 endDate가 같을 때 -> 성공")
        void same_startDate_endDate() {
            // given
            LocalDate startDate = LocalDate.of(2023, 4, 19);
            LocalDate endDate = LocalDate.of(2023, 4, 19);

            // when
            TripPeriod period = TripPeriod.of(startDate, endDate);

            // then
            assertThat(period.getStartDate()).isEqualTo(startDate);
            assertThat(period.getEndDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("시작일보다 늦은 종료일이고, 일수가 10일보다 작음 -> 성공")
        void startDate_after_endDate() {
            // given
            LocalDate startDate = LocalDate.of(2023, 4, 19);
            LocalDate endDate = LocalDate.of(2023, 4, 20);

            // when
            TripPeriod period = TripPeriod.of(startDate, endDate);

            // then
            assertThat(period.getStartDate()).isEqualTo(startDate);
            assertThat(period.getEndDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("시작일보다 늦은 종료일이고, 일수가 10일 -> 성공")
        void startDate_after_endDate_10Day() {
            // given
            LocalDate startDate = LocalDate.of(2023, 3, 1);
            LocalDate endDate = LocalDate.of(2023, 3, 10);

            // when
            TripPeriod period = TripPeriod.of(startDate, endDate);

            // then
            assertThat(period.getStartDate()).isEqualTo(startDate);
            assertThat(period.getEndDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("시작일만 null -> 예외 발생")
        void startDateNull() {
            // given
            LocalDate startDate = null;
            LocalDate endDate = LocalDate.of(2023, 4, 20);

            // when & then
            assertThatThrownBy(() -> TripPeriod.of(startDate, endDate)).isInstanceOf(InvalidPeriodException.class);
        }

        @Test
        @DisplayName("종료일만 null -> 예외 발생")
        void endDate_Null() {
            // given
            LocalDate startDate = LocalDate.of(2023, 4, 20);
            LocalDate endDate = null;

            // when & then
            assertThatThrownBy(() -> TripPeriod.of(startDate, endDate)).isInstanceOf(InvalidPeriodException.class);
        }

        @Test
        @DisplayName("시작일보다 앞서는 종료일 -> 예외 발생")
        void endDate_is_Before_StartDate() {
            // given
            LocalDate startDate = LocalDate.of(2023, 4, 20);
            LocalDate endDate = LocalDate.of(2023, 4, 19);

            // when & then
            assertThatThrownBy(() -> TripPeriod.of(startDate, endDate)).isInstanceOf(InvalidPeriodException.class);
        }

        @Test
        @DisplayName("여행 기간이 10일 초과 -> 예외 발생")
        public void tooLongPeriod() {
            // given
            LocalDate startDate = LocalDate.of(2023, 5, 1);
            LocalDate endDate = LocalDate.of(2023, 5, 11);

            // when & then
            assertThatThrownBy(() -> TripPeriod.of(startDate, endDate))
                    .isInstanceOf(TooLongPeriodException.class);
        }
    }

    /**
     * {@link TripPeriod#empty()} 를 통해 생성된 여행은 시작일/종료일이 null임을 테스트합니다.
     */
    @Test
    @DisplayName("TripPeriod.empty() 로 생성된 여행은 시작일, 종료일이 null이다.")
    void emptyTripPeriodTest() {
        TripPeriod emptyPeriod = TripPeriod.empty();

        assertThat(emptyPeriod.getStartDate()).isNull();
        assertThat(emptyPeriod.getEndDate()).isNull();
    }

    /**
     * 어떤 여행 기간과, 다른 여행 기간 사이의 겹치는 기간을 구하는 기능을 테스트합니다.
     */
    @Nested
    @DisplayName("Intersection 테스트")
    class IntersectionTest {

        /**
         * 서로 겹치지 않는 기간 사이의 겹치지 않는 기간을 구하는 기능을 테스트합니다. 비어있는 기간이 나와야합니다.
         */
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

        /**
         * 서로 중복되는 기간이 있는 기간들끼리, 겹치는 기간을 구하는 기능을 테스트합니다.
         */
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

    /**
     * TripPeriod를 통해 날짜들의 Stream을 얻어오는 기능을 테스트합니다.
     */
    @Nested
    @DisplayName("DateStreamTest")
    class DateStreamTest {

        /**
         * 비어있는 TripPeriod를 통해 날짜들의 Stream을 얻어올 때 빈 Stream이 얻어짐을 테스트합니다.
         */
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

        /**
         * 비어있지 않은 TripPeriod를 통해 날짜들의 Stream을 얻어올 때 연속된 날짜들의 Stream이 얻어짐을 테스트합니다.
         */
        @Test
        @DisplayName("비어있지 않은 기간의 dateStream은 시작일부터 종료일까지 날짜들의 Stream이다.")
        public void notEmptyPeriod_create_dateStream() {
            // given
            TripPeriod tripPeriod = TripPeriod.of(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5));

            // when
            Stream<LocalDate> dateStream = tripPeriod.dateStream();

            // then
            List<LocalDate> dates = dateStream.toList();
            assertThat(dates).containsExactly(
                    LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2),
                    LocalDate.of(2023, 1, 3), LocalDate.of(2023, 1, 4),
                    LocalDate.of(2023, 1, 5));
        }
    }
}
