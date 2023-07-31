package com.cosain.trilo.unit.trip.domain.entity;

import com.cosain.trilo.common.exception.day.InvalidTripDayException;
import com.cosain.trilo.common.exception.schedule.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.common.exception.schedule.MidScheduleIndexConflictException;
import com.cosain.trilo.common.exception.schedule.ScheduleIndexRangeException;
import com.cosain.trilo.common.exception.trip.EmptyPeriodUpdateException;
import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.cosain.trilo.trip.domain.vo.ScheduleIndex.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 여행 도메인의 테스트 코드입니다.
 */
@DisplayName("Trip(여행) 도메인 테스트")
public class TripTest {


    /**
     * 여행을 생성할 때의 테스트입니다.
     */
    @Test
    @DisplayName("여행 생성 -> 성공")
    void createTest() {
        // given
        TripTitle tripTitle = TripTitle.of("제목");
        Long tripperId = 1L;

        // when
        Trip trip = Trip.create(tripTitle, tripperId);

        // then

        // 생성 시 주입한 값이 잘 주입됐는 지 테스트
        assertThat(trip.getTripperId()).isEqualTo(tripperId);
        assertThat(trip.getTripTitle()).isEqualTo(tripTitle);

        // 기본으로 초기화되는 필드들 테스트
        assertThat(trip.getStatus()).isSameAs(TripStatus.UNDECIDED);
        assertThat(trip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(trip.getTripImage()).isEqualTo(TripImage.defaultImage());
    }

    /**
     * 여행 제목 수정 기능 테스트입니다.
     */
    @Test
    @DisplayName("여행 제목 수정 -> 성공")
    void testTripTitleChange() {
        // given
        TripTitle beforeTitle = TripTitle.of("변경 전 제목");
        Long tripperId = 1L;
        Trip trip = Trip.create(beforeTitle, tripperId);

        // when
        TripTitle newTitle = TripTitle.of("변경 후 제목");
        trip.changeTitle(newTitle);

        // then

        // 제목 수정됨
        assertThat(trip.getTripTitle()).isNotEqualTo(beforeTitle);
        assertThat(trip.getTripTitle()).isEqualTo(newTitle);
    }

    /**
     * 여행 기간 수정 기능 테스트들입니다.
     */
    @Nested
    @DisplayName("ChangePeriod 테스트")
    class ChangePeriodTest {

        /**
         * 기간이 정해져있지 않은 여행의 기간을 비어있는 기간으로 변경하는 상황을 테스트합니다.
         */
        @Test
        @DisplayName("UNDECIDED 상태의 여행을 비어있는 기간으로 수정 -> 변경 없음")
        void testUndecidedTrip_to_EmptyPeriod() {
            long tripId = 1L;
            long tripperId = 2L;
            Trip undecidedTrip = TripFixture.undecided_Id(tripId, tripperId);
            TripPeriod emptyPeriod = TripPeriod.empty();

            // when
            var changePeriodResult = undecidedTrip.changePeriod(emptyPeriod); // 비어있는 기간으로 변경

            // then
            // 여행의 상태, 기간 변경 없음
            assertThat(undecidedTrip.getStatus()).isSameAs(TripStatus.UNDECIDED);
            assertThat(undecidedTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());

            // 여행이 가진 Day도 변화 없음
            assertThat(undecidedTrip.getDays()).isEmpty();

            // 삭제된 Day 없음
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds).isEmpty();

            // 생성된 Day 없음
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).isEmpty();
        }

        /**
         * 기간이 정해져 있지 않은 여행을 비어있지 않은 기간으로 변경하는 상황을 테스트합니다.
         */
        @Test
        @DisplayName("UNDECIDED 상태의 여행의 기간을 비어있지 않은 기간으로 변경(초기화)할 때")
        void emptyPeriod_to_NotEmptyPeriod() {
            // given
            long tripId = 1L;
            long tripperId = 2L;
            Trip undecidedTrip = TripFixture.undecided_Id(tripId, tripperId);
            TripPeriod newPeriod = TripPeriod.of(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 3));

            // when
            var changePeriodResult = undecidedTrip.changePeriod(newPeriod);

            // then

            // 여행의 상태/기간 변경됨
            assertThat(undecidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
            assertThat(undecidedTrip.getTripPeriod()).isEqualTo(newPeriod);

            // 여행이 새로 생성된 Day들을 가짐
            assertThat(undecidedTrip.getDays()).map(Day::getTripDate).containsExactly(
                    LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2),
                    LocalDate.of(2023, 1, 3));

            // 삭제되는 Day 없음
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds).isEmpty();

            // Day들이 생성됨
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).map(Day::getTripDate).containsExactly(
                    LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2),
                    LocalDate.of(2023, 1, 3));
            assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull); // 색상을 가짐
        }

        /**
         * 기간이 정해진 여행을 비어있는 기간으로 변경하려 할 때 예외가 발생함을 검증합니다.
         */
        @Test
        @DisplayName("기간이 정해진(DECIDED) 여행을 비어있는 기간으로 수정 -> 예외 발생")
        void decidedTrip_To_EmptyPeriod() {
            Long tripId = 1L;
            Long tripperId = 2L;

            Trip decidedTrip = TripFixture.decided_Id(tripId, tripperId, LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5), 1L);
            TripPeriod emptyPeriod = TripPeriod.empty();

            // when & then

            // 기간 정해진 여행을 빈 기간으로 수정 -> 예외 발생
            assertThatThrownBy(() -> decidedTrip.changePeriod(emptyPeriod))
                    .isInstanceOf(EmptyPeriodUpdateException.class);
        }

        /**
         * 기간이 정해진 여행을 겹치는 여행을 겹치지 않는 다른 기간으로 수정하는 상황을 테스트합니다.
         */
        @Test
        @DisplayName("기간이 정해진(DECIDED) 여행을 겹치지 않는 다른 기간으로 수정 -> 성공")
        void decidedTrip_Period_To_NonOverlappedPeriod() {
            // given
            Long tripId = 1L;
            Long tripperId = 2L;

            LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);

            LocalDate afterStartDate = LocalDate.of(2023, 3, 3);
            LocalDate afterEndDate = LocalDate.of(2023, 3, 4);

            Trip decidedTrip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);
            Day day1 = decidedTrip.getDays().get(0);
            Day day2 = decidedTrip.getDays().get(1);

            TripPeriod notOverlappedPeriod = TripPeriod.of(afterStartDate, afterEndDate);

            // when
            var changePeriodResult = decidedTrip.changePeriod(notOverlappedPeriod); // 겹치지 않는 기간으로 수정

            // then

            // 여행의 상태/기간 검증
            assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
            assertThat(decidedTrip.getTripPeriod()).isEqualTo(notOverlappedPeriod);

            // 여행은 새로 수정된 기간의 Day만을 가짐
            assertThat(decidedTrip.getDays())
                    .map(Day::getTripDate)
                    .containsExactly(afterStartDate, afterEndDate);

            // 새로운 기간에 속하지 않는 기존 기간들이 삭제됨
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds.size()).isEqualTo(2);
            assertThat(deletedDayIds).containsExactly(day1.getId(), day2.getId());

            // 기존 기간에 속해있지 않았던, 새로운 기간에 속하는 Day들이 생성됨
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull); // 색상을 가짐
            assertThat(createdDays).map(Day::getTripDate).containsExactly(afterStartDate, afterEndDate);
        }

        /**
         * 기간이 정해진 여행을 뒤에서 겹치는 기간으로 수정하는 상황을 테스트합니다.
         */
        @Test
        @DisplayName("기간이 정해진 여행을 뒤에서 겹치는 기간으로 수정 -> 성공")
        void decidedTrip_To_back_overlapped_Period() {
            // given
            Long tripId = 1L;
            Long tripperId = 2L;

            LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);

            LocalDate afterStartDate = LocalDate.of(2023, 3, 2);
            LocalDate afterEndDate = LocalDate.of(2023, 3, 3);

            Trip decidedTrip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);
            Day day1 = decidedTrip.getDays().get(0);
            Day day2 = decidedTrip.getDays().get(1);

            TripPeriod backOverlappedPeriod = TripPeriod.of(afterStartDate, afterEndDate);

            // when
            var changePeriodResult = decidedTrip.changePeriod(backOverlappedPeriod); // 기존 기간과 뒤에서 겹치는 기간으로 수정

            // then
            // 여행의 상태/기간 검증
            assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
            assertThat(decidedTrip.getTripPeriod()).isEqualTo(backOverlappedPeriod);

            // 여행은 변경된 기간에 해당하는 Day들만 가지고 있음
            assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactly(
                    LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 3));


            // 새로운 기간에 속하지 않는 기존 기간들이 삭제됨
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds).containsExactly(day1.getId());

            // 기존 기간에 속해있지 않았던, 새로운 기간에 속하는 Day들이 생성됨
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull); // 색상을 가짐
            assertThat(createdDays).map(Day::getTripDate).containsExactly(LocalDate.of(2023, 3, 3));
        }

        /**
         * 기간이 정해진 여행을 앞에서 겹치는 기간으로 수정하는 경우를 테스트합니다.
         */
        @Test
        @DisplayName("기간이 정해진(DECIDED) 여행을 앞에서 겹치는 기간으로 수정 -> 성공")
        void decidedTrip_To_front_overlapped_Period() {
            // given
            Long tripId = 1L;
            Long tripperId = 2L;

            LocalDate beforeStartDate = LocalDate.of(2023, 3, 2);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 3); // 새로운 기간에 속하지 않으므로 제거됨

            LocalDate afterStartDate = LocalDate.of(2023, 3, 1); // 새로 생성됨
            LocalDate afterEndDate = LocalDate.of(2023, 3, 2); // 중복됨

            Trip decidedTrip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);
            Day day1 = decidedTrip.getDays().get(0);
            Day day2 = decidedTrip.getDays().get(1);

            TripPeriod frontOverlappedPeriod = TripPeriod.of(afterStartDate, afterEndDate);

            // when
            var changePeriodResult = decidedTrip.changePeriod(frontOverlappedPeriod);

            // then

            // 여행의 상태/기간 검증
            assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
            assertThat(decidedTrip.getTripPeriod()).isEqualTo(frontOverlappedPeriod);

            // 여행은 변경된 기간에 해당하는 Day들만 가지고 있음
            assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactlyInAnyOrder(
                    LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2));

            // 새로운 기간에 속하지 않는 기존 기간들이 삭제됨
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds).containsExactly(day2.getId());

            // 기존 기간에 속해있지 않았던, 새로운 기간에 속하는 Day들이 생성됨
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull); // 색상을 가짐
            assertThat(createdDays).map(Day::getTripDate).containsExactly(LocalDate.of(2023, 3, 1));
        }

        /**
         * 기간이 정해진 여행을 내부에 포함된 기간으로 수정하는 경우를 테스트합니다.
         */
        @Test
        @DisplayName("기간이 정해진(DECIDED) 여행을 내부에 포함된 기간으로 수정 -> 성공")
        void decidedTrip_To_inner_overlapped_Period() {
            // given
            Long tripId = 1L;
            Long tripperId = 2L;

            LocalDate beforeStartDate = LocalDate.of(2023, 3, 1);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 3);

            LocalDate afterStartDate = LocalDate.of(2023, 3, 2);
            LocalDate afterEndDate = LocalDate.of(2023, 3, 2);

            Trip decidedTrip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);
            Day day1 = decidedTrip.getDays().get(0);
            Day day2 = decidedTrip.getDays().get(1);
            Day day3 = decidedTrip.getDays().get(2);

            TripPeriod innerOverlappedPeriod = TripPeriod.of(afterStartDate, afterEndDate);

            // then
            var changePeriodResult = decidedTrip.changePeriod(innerOverlappedPeriod);

            // then

            // 여행의 상태/기간 검증
            assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
            assertThat(decidedTrip.getTripPeriod()).isEqualTo(innerOverlappedPeriod);

            // 여행은 변경된 기간에 해당하는 Day들만 가지고 있음
            assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactlyInAnyOrder(
                    LocalDate.of(2023, 3, 2));

            // 새로운 기간에 속하지 않는 기존 기간들이 삭제됨
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds).containsExactlyInAnyOrder(day1.getId(), day3.getId());

            // 새로 생기는 Day 없음
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).isEmpty();
        }

        /**
         * 기간이 정해진 여행을 기존 기간을 포함하는 더 큰 기간으로 수정하는 경우를 테스트합니다.
         */
        @Test
        @DisplayName("기간이 정해진(DECIDED) 여행을, 기존 기간을 포함하는 더 큰 기간으로 수정 -> 성공")
        void decidedTrip_To_outer_overlapped_Period() {
            // given
            Long tripId = 1L;
            Long tripperId = 2L;


            LocalDate beforeStartDate = LocalDate.of(2023, 3, 2);
            LocalDate beforeEndDate = LocalDate.of(2023, 3, 2);

            LocalDate afterStartDate = LocalDate.of(2023, 3, 1);
            LocalDate afterEndDate = LocalDate.of(2023, 3, 3);


            Trip decidedTrip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);
            Day day1 = decidedTrip.getDays().get(0);

            TripPeriod outerOverlappedPeriod = TripPeriod.of(afterStartDate, afterEndDate);

            // when
            var changePeriodResult = decidedTrip.changePeriod(outerOverlappedPeriod);

            // then

            // 여행의 상태/기간 검증
            assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
            assertThat(decidedTrip.getTripPeriod()).isEqualTo(outerOverlappedPeriod);

            // 여행은 변경된 기간에 해당하는 Day들만 가지고 있음
            assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactlyInAnyOrder(
                    LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2),
                    LocalDate.of(2023, 3, 3));

            // 삭제된 Day 없음
            List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
            assertThat(deletedDayIds).isEmpty();

            // 기존 기간에 속해있지 않았던, 새로운 기간에 속하는 Day들이 생성됨
            List<Day> createdDays = changePeriodResult.getCreatedDays();
            assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull); // 색상을 가짐
            assertThat(createdDays).map(Day::getTripDate)
                    .containsExactly(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3));
        }
    }

    @Nested
    @DisplayName("CreateSchedule 테스트")
    class CreateScheduleTest {

        ScheduleTitle scheduleTitle = ScheduleTitle.of("일정 제목");
        Place place = Place.of("place-id", "place-name", Coordinate.of(31.123, 123.123));

        @DisplayName("임시보관함에 일정을 새로 추가할 경우")
        @Nested
        class Case_TemporaryStorage {

            @DisplayName("인덱스가 최소값을 벗어나면, ScheduleIndexRangeException 발생")
            @Test
            public void when_new_index_range_is_under_max_index_value_then_it_throws_ScheduleIndexRangeException() {
                Trip trip = TripFixture.undecided_nullId(1L);
                Schedule schedule1 = ScheduleFixture.temporaryStorage_NullId(trip, MIN_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.createSchedule(null, scheduleTitle, place))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("인덱스가 최대 범위를 벗어나지 않으면, 정상적으로 더 작은 순서의 Schedule 생성됨")
            @Test
            public void successTest() {
                Trip trip = TripFixture.undecided_nullId(1L);

                Schedule schedule1 = trip.createSchedule(null, scheduleTitle, place);
                Schedule schedule2 = trip.createSchedule(null, scheduleTitle, place);

                assertThat(schedule1.getScheduleIndex()).isEqualTo(ZERO_INDEX);
                assertThat(schedule2.getScheduleIndex()).isEqualTo(of(-DEFAULT_SEQUENCE_GAP));

                // 생성된 일정시간들은 디폴트 시간
                assertThat(schedule1.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
                assertThat(schedule2.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());

                // 생성된 일정제목들은 디폴트 제목
                assertThat(schedule1.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
                assertThat(schedule2.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
            }
        }

        @DisplayName("특정 Day에 일정을 새로 추가할 경우")
        @Nested
        class Case_DaySchedule {

            @DisplayName("소속된 Day가 아닌 곳에 Day를 생성하라고 요청할 경우 InvalidTripDayException 발생")
            @Test
            public void when_trip_not_contains_day_then_it_throws_InvalidTripDayException() {
                // given
                Trip trip = TripFixture.decided_Id(1L, 1L, LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1), 1L);
                Day validDay = trip.getDays().get(0);

                Trip otherTrip = TripFixture.decided_Id(2L, 2L, LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1), 2L);
                Day otherTripDay = otherTrip.getDays().get(0);

                // when & then
                assertThatThrownBy(() -> trip.createSchedule(otherTripDay, scheduleTitle, place))
                        .isInstanceOf(InvalidTripDayException.class);
            }

            @DisplayName("인덱스가 최대 범위를 벗어나면, ScheduleIndexRangeException 발생")
            @Test
            public void when_new_index_range_is_over_max_index_value_then_it_throws_ScheduleIndexRangeException() {
                Trip trip = TripFixture.decided_Id(1L, 1L, LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1), 1L);
                Day day = trip.getDays().get(0);
                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, day, MAX_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.createSchedule(day, scheduleTitle, place))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("인덱스가 최대 범위를 벗어나지 않으면, 정상적으로 다음 순서의 Schedule 생성됨")
            @Test
            public void successTest() {
                Trip trip = TripFixture.decided_Id(1L, 1L, LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1), 1L);
                Day day = trip.getDays().get(0);

                Schedule schedule1 = trip.createSchedule(day, scheduleTitle, place);
                Schedule schedule2 = trip.createSchedule(day, scheduleTitle, place);

                assertThat(schedule1.getScheduleIndex()).isEqualTo(ZERO_INDEX);
                assertThat(schedule2.getScheduleIndex()).isEqualTo(of(DEFAULT_SEQUENCE_GAP));

                // 생성된 일정시간들은 디폴트 시간
                assertThat(schedule1.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
                assertThat(schedule2.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());

                // 생성된 일정제목들은 디폴트 제목
                assertThat(schedule1.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
                assertThat(schedule2.getScheduleContent()).isEqualTo(ScheduleContent.defaultContent());
            }
        }
    }

    @Nested
    @DisplayName("MoveSchedule 테스트")
    class MoveScheduleTest {

        @Nested
        @DisplayName("임시보관함에서 임시보관함으로 옮길 떄")
        class Case_From_TemporaryStorage_To_TemporaryStorage {

            @DisplayName("targetOrder가 0보다 작으면 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_under_zero_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                // given
                Trip trip = TripFixture.undecided_nullId(1L);
                Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, null, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 임시보관함 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_temporary_storage_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(1L, trip, 100L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 3))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("자신의 기존 순서로 이동할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_same_position_then_nothing_changed() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(1L, trip, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, null, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                Schedule firstSchedule = temporaryStorage.get(0);
                Schedule secondSchedule = temporaryStorage.get(1);

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex()).isEqualTo(schedule1.getScheduleIndex());
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }

            @DisplayName("자신의 순서값 다음 값으로 이동시키려 할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_after_currentOrder_then_nothing_changed() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, null, 2);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                Schedule firstSchedule = temporaryStorage.get(0);
                Schedule secondSchedule = temporaryStorage.get(1);

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex()).isEqualTo(schedule1.getScheduleIndex());
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }

            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_TemporaryStorageSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, null, 2);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().generateNextIndex());
                assertThat(schedule2.getScheduleIndex().getValue()).isEqualTo(100L);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_TemporaryStorageSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);
                Day day = null;

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, MAX_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, day, 2))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞으로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_Safe_then_schedule_move_to_Head() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, null, 0);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule2.getScheduleIndex()).isEqualTo(schedule1.getScheduleIndex().generateBeforeIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);
                Day day = null;

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, MIN_INDEX_VALUE);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule2, day, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 유효한 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 100L);
                Schedule schedule3 = ScheduleFixture.temporaryStorage_Id(3L, trip, 200L);

                // when (schedule1을 2번 순서로)
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, null, 2);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                assertThat(temporaryStorage.size()).isEqualTo(3);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Long tripId = 1L;
                Long tripperId = 2L;
                Trip trip = TripFixture.undecided_Id(tripId, tripperId);
                Day day = null;

                Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 10L);
                Schedule schedule3 = ScheduleFixture.temporaryStorage_Id(3L, trip, 11L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, day, 2))
                        .isInstanceOf(MidScheduleIndexConflictException.class);
            }
        }

        @Nested
        @DisplayName("임시보관함에서 어떤 Day로 옮길 때")
        class Case_From_TemporaryStorage_To_Day {

            @Test
            @DisplayName("targetDay가 Trip의 Day가 아니면, InvalidTripDayException 발생")
            public void when_targetDay_is_not_in_trip_then_it_throws_InvalidTripDayException() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                Long otherTripId = 3L;

                Trip trip = TripFixture.undecided_Id(tripId, tripperId);
                Schedule schedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);

                LocalDate startDate = LocalDate.of(2023, 4, 1);
                LocalDate endDate = LocalDate.of(2023, 4, 1);
                Trip otherTrip = TripFixture.decided_Id(otherTripId, tripperId, startDate, endDate, 1L);
                Day targetDay = otherTrip.getDays().get(0);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, 0))
                        .isInstanceOf(InvalidTripDayException.class);
            }

            @DisplayName("targetOrder가 0보다 작으면 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_under_zero_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);
                Schedule schedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 Schedules 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_day_schedules_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);

                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(moveSchedule, targetDay, 2))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }


            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);

                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule = ScheduleFixture.day_Id(1L, trip, targetDay, 0L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(moveSchedule, targetDay, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> targetDaySchedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(targetDaySchedules.size()).isEqualTo(2);
                assertThat(targetDaySchedules).containsExactlyInAnyOrder(moveSchedule, targetDaySchedule);
                assertThat(moveSchedule.getScheduleIndex()).isEqualTo(targetDaySchedule.getScheduleIndex().generateNextIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);
                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule = ScheduleFixture.day_Id(2L, trip, targetDay, MAX_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(moveSchedule, targetDay, 1))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_isSafe_schedule_move_to_Head() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);

                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(moveSchedule, targetDay, 0);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(2);
                assertThat(schedules).containsExactlyInAnyOrder(moveSchedule, targetDaySchedule);
                assertThat(moveSchedule.getScheduleIndex()).isEqualTo(targetDaySchedule.getScheduleIndex().generateBeforeIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);
                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule = ScheduleFixture.day_Id(2L, trip, targetDay, MIN_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(moveSchedule, targetDay, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);

                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule1 = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);
                Schedule targetDaySchedule2 = ScheduleFixture.day_Id(3L, trip, targetDay, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(moveSchedule, targetDay, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(3);
                assertThat(schedules).containsExactlyInAnyOrder(moveSchedule, targetDaySchedule1, targetDaySchedule2);
                assertThat(moveSchedule.getScheduleIndex()).isEqualTo(targetDaySchedule1.getScheduleIndex().mid(targetDaySchedule2.getScheduleIndex()));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day targetDay = trip.getDays().get(0);

                Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(1L, trip, 0L);
                Schedule targetDaySchedule1 = ScheduleFixture.day_Id(2L, trip, targetDay, 10L);
                Schedule targetDaySchedule2 = ScheduleFixture.day_Id(3L, trip, targetDay, 11L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(moveSchedule, targetDay, 1))
                        .isInstanceOf(MidScheduleIndexConflictException.class);
            }
        }

        @Nested
        @DisplayName("Day에서 Day로 옮길 때")
        class Case_From_Day_To_Day {

            @Test
            @DisplayName("targetDay가 Trip의 Day가 아니면, InvalidTripDayException 발생")
            public void when_targetDay_is_not_in_trip_then_it_throws_InvalidTripDayException() {
                // given
                Long tripId = 1L;
                Long otherTripId = 2L;
                Long tripperId = 3L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Schedule schedule = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Trip otherTrip = TripFixture.decided_Id(otherTripId, tripperId, startDate, endDate, 3L);
                Day targetDay = otherTrip.getDays().get(1);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, 0))
                        .isInstanceOf(InvalidTripDayException.class);
            }

            @DisplayName("targetOrder가 0보다 작으면 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_under_zero_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 Schedules 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_day_schedules_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 2))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("같은 Day의 기존의 순서로 이동할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_same_day_and_same_position_then_nothing_changed() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day day = trip.getDays().get(0);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, day, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, day, 100L);


                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 1);

                // then
                List<Schedule> schedules = day.getSchedules();
                Schedule firstSchedule = schedules.get(0);
                Schedule secondSchedule = schedules.get(1);

                assertThat(schedules.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex().getValue()).isEqualTo(0L);
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex().getValue()).isEqualTo(100L);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }

            @DisplayName("같은 Day의 기존의 순서 다음으로 이동시키려 할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_same_day_and_after_currentOrder_then_nothing_changed() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day day = trip.getDays().get(0);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, day, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, day, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 2);

                // then
                List<Schedule> schedules = day.getSchedules();
                Schedule firstSchedule = schedules.get(0);
                Schedule secondSchedule = schedules.get(1);

                assertThat(schedules.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex().getValue()).isEqualTo(0L);
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex().getValue()).isEqualTo(100L);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }


            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> targetDaySchedules = targetDay.getSchedules();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(targetDaySchedules.size()).isEqualTo(2);
                assertThat(targetDaySchedules).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().generateNextIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, MAX_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞으로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_isSafe_schedule_move_to_Head() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 0);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> targetDaySchedules = targetDay.getSchedules();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(targetDaySchedules.size()).isEqualTo(2);
                assertThat(targetDaySchedules).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().generateBeforeIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, MIN_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, 0L);
                Schedule schedule3 = ScheduleFixture.day_Id(3L, trip, targetDay, 100L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(3);
                assertThat(schedules).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, targetDay, 10L);
                Schedule schedule3 = ScheduleFixture.day_Id(3L, trip, targetDay, 11L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(MidScheduleIndexConflictException.class);
            }
        }

        @Nested
        @DisplayName("Day에서 임시보관함으로 옮길 때")
        class Case_From_Day_To_TemporaryStorage {

            @DisplayName("targetOrder가 0보다 작으면 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_under_zero_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                // given
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 임시보관함 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_temporary_storage_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 0L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 2))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }


            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_temporaryStorageSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 2);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 0L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().generateNextIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_temporaryStorageSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, MAX_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }


            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞으로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_Head_scheduleIndex_isSafe_schedule_move_to_Head() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 0L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 0);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().generateBeforeIndex());
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_Head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, MIN_INDEX_VALUE);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 0L);
                Schedule schedule3 = ScheduleFixture.temporaryStorage_Id(3L, trip, 1000L);

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> schedules = beforeDay.getSchedules();
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(schedules).isEmpty();
                assertThat(temporaryStorage.size()).isEqualTo(3);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Long tripId = 1L;
                Long tripperId = 2L;
                LocalDate startDate = LocalDate.of(2023, 3, 1);
                LocalDate endDate = LocalDate.of(2023, 3, 1);

                Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, 1L);
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = ScheduleFixture.day_Id(1L, trip, beforeDay, 0L);
                Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 10L);
                Schedule schedule3 = ScheduleFixture.temporaryStorage_Id(3L, trip, 11L);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(MidScheduleIndexConflictException.class);
            }
        }
    }
}
