package com.cosain.trilo.unit.trip.domain.entity;

import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.exception.*;
import com.cosain.trilo.trip.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.cosain.trilo.fixture.TripFixture.UNDECIDED_TRIP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[TripCommand] Trip 테스트")
public class TripTest {

    @Nested
    @DisplayName("여행을 create로 생성하면")
    class When_Create {

        // given
        private final TripTitle title = TripTitle.of("제목");
        private final Long tripperId = 1L;

        @Test
        @DisplayName("지정한 이름을 가진 여행이 생성된다.")
        public void createdTrip_has_same_title() {
            // when
            Trip trip = Trip.create(title, tripperId);

            // then
            assertThat(trip.getTripTitle().getValue()).isEqualTo("제목");
        }

        @Test
        @DisplayName("UNDECIEDE 상태를 가진 여행이 생성된다.")
        public void createdTrip_status_is_undecided() {
            // when
            Trip trip = Trip.create(title, tripperId);

            // then
            assertThat(trip.getStatus()).isSameAs(TripStatus.UNDECIDED);
        }
    }

    @Nested
    @DisplayName("여행의 제목을 changeTitle로 변경하면")
    class When_ChangeTitle {

        @Test
        @DisplayName("새로운 title이 변경되어 적용된다.")
        public void trip_has_changed_title() {
            // given
            TripTitle beforeTitle = TripTitle.of("변경 전 제목");
            Long tripperId = 1L;
            Trip trip = Trip.create(beforeTitle, tripperId);

            // when
            TripTitle newTitle = TripTitle.of("변경 후 제목");
            trip.changeTitle(newTitle);

            // then
            assertThat(trip.getTripTitle()).isEqualTo(newTitle);
        }

    }

    @Nested
    @DisplayName("ChangePeriod 테스트")
    class ChangePeriodTest {

        @Nested
        @DisplayName("UNDECIDED 상태 여행을 수정할 때")
        class Case_Change_UNDECIDED_Trip {

            // common given
            private Trip undecidedTrip = UNDECIDED_TRIP.createUndecided(1L, 1L, "여행 제목");

            @Nested
            @DisplayName("비어있는 기간으로 변경하면")
            class If_Change_To_EmptyPeriod {
                // given
                TripPeriod emptyPeriod = TripPeriod.empty();

                @Test
                @DisplayName("삭제된 날짜가 없다. (원래 Day가 없었고, 변경이 없었으므로)")
                public void there_are_no_deletedDays() {
                    // when
                    var changePeriodResult = undecidedTrip.changePeriod(emptyPeriod);

                    // then
                    List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                    assertThat(deletedDayIds).isEmpty();
                }

                @Test
                @DisplayName("생성된 날짜가 없다.")
                public void there_are_no_createdDays() {
                    // when
                    var changePeriodResult = undecidedTrip.changePeriod(emptyPeriod);

                    // then
                    List<Day> createdDays = changePeriodResult.getCreatedDays();
                    assertThat(createdDays).isEmpty();
                }

                @Test
                @DisplayName("Trip이 가진 Days는 여전히 비어있다.")
                public void trip_still_has_noDays() {
                    // when
                    undecidedTrip.changePeriod(emptyPeriod);

                    // then
                    assertThat(undecidedTrip.getDays()).isEmpty();
                }

                @Test
                @DisplayName("Trip은 여전히 Undecided 상태이다.")
                public void trip_status_is_still_undecided() {
                    // when
                    undecidedTrip.changePeriod(emptyPeriod);

                    // then
                    assertThat(undecidedTrip.getStatus()).isSameAs(TripStatus.UNDECIDED);
                }

                @Test
                @DisplayName("Trip의 기간은 여전히 비어있는 기간이다.")
                public void trip_period_is_still_emptyPeriod() {
                    // when
                    undecidedTrip.changePeriod(emptyPeriod);

                    // then
                    assertThat(undecidedTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
                }
            }

            @Nested
            @DisplayName("비어있지 않은 기간으로 변경하면")
            class If_Change_To_NotEmptyPeriod {
                // given
                TripPeriod newPeriod = TripPeriod.of(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 3));

                @Test
                @DisplayName("삭제된 날짜가 없다. (원래 Day가 없었으므로)")
                public void there_are_no_deletedDays() {
                    // when
                    var changePeriodResult = undecidedTrip.changePeriod(newPeriod);

                    // then
                    List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                    assertThat(deletedDayIds).isEmpty();
                }

                @Test
                @DisplayName("새로 Day들이 생성된다")
                public void then_newDays_created() {
                    // when
                    var changePeriodResult = undecidedTrip.changePeriod(newPeriod);

                    // then
                    List<Day> createdDays = changePeriodResult.getCreatedDays();
                    assertThat(createdDays).map(Day::getTripDate).containsExactly(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 3));
                }

                @Test
                @DisplayName("모든 생성된 Day들은 색상을 가진다.")
                public void all_createdDays_have_color() {
                    // when
                    var changePeriodResult = undecidedTrip.changePeriod(newPeriod);

                    // then
                    List<Day> createdDays = changePeriodResult.getCreatedDays();
                    assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull);
                }

                @Test
                @DisplayName("Trip은 새로 생긴 날짜들을 가진다.")
                public void trip_still_has_newDays() {
                    // when
                    undecidedTrip.changePeriod(newPeriod);


                    // then
                    assertThat(undecidedTrip.getDays()).map(Day::getTripDate).containsExactly(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 3));
                }

                @Test
                @DisplayName("Trip은 decided 상태로 변경된다.")
                public void trip_status_is_changed_to_decided() {
                    // when
                    undecidedTrip.changePeriod(newPeriod);

                    // then
                    assertThat(undecidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
                }

                @Test
                @DisplayName("Trip의 기간은 새로운 기간으로 변경된 상태다.")
                public void trip_period_is_equalTo_newPeriod() {
                    // when
                    undecidedTrip.changePeriod(newPeriod);

                    // then
                    assertThat(undecidedTrip.getTripPeriod()).isEqualTo(newPeriod);
                }
            }
        }

        @Nested
        @DisplayName("DECIDED 상태 여행을 수정할 때")
        class Case_Change_DECIDED_Trip {

            // common given
            Trip decidedTrip;
            Day day1, day2, day3, day4, day5;
            TripPeriod notOverlappedPeriod = TripPeriod.of(LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 10));

            @BeforeEach
            void setUp() {
                Long tripId = 1L;
                Long tripperId = 2L;

                decidedTrip = Trip.builder()
                        .id(tripId)
                        .tripperId(tripperId)
                        .tripTitle(TripTitle.of("여행 제목"))
                        .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5)))
                        .status(TripStatus.DECIDED)
                        .build();

                day1 = Day.builder()
                        .id(1L)
                        .trip(decidedTrip)
                        .tripDate(LocalDate.of(2023, 3, 1))
                        .build();

                day2 = Day.builder()
                        .id(2L)
                        .trip(decidedTrip)
                        .tripDate(LocalDate.of(2023, 3, 2))
                        .build();

                day3 = Day.builder()
                        .id(3L)
                        .trip(decidedTrip)
                        .tripDate(LocalDate.of(2023, 3, 3))
                        .build();

                day4 = Day.builder()
                        .id(4L)
                        .trip(decidedTrip)
                        .tripDate(LocalDate.of(2023, 3, 4))
                        .build();

                day5 = Day.builder()
                        .id(5L)
                        .trip(decidedTrip)
                        .tripDate(LocalDate.of(2023, 3, 5))
                        .build();

                decidedTrip.getDays().addAll(List.of(day1, day2, day3, day4, day5));
            }

            @Test
            @DisplayName("빈 기간으로 수정하면 EmptyPeriodUpdateException이 발생한다.")
            public void if_change_to_emptyPeriod_then_it_throws_EmptyPeriodUpdateException() {
                // given
                TripPeriod emptyPeriod = TripPeriod.empty();

                // when & then
                assertThatThrownBy(() -> decidedTrip.changePeriod(emptyPeriod)).isInstanceOf(EmptyPeriodUpdateException.class);
            }

            @Nested
            @DisplayName("겹치지 않는 기간으로 수정하면")
            class If_Change_To_NotOverlappedPeriod {

                @Test
                @DisplayName("기존 기간의 Day들이 삭제된다")
                public void previous_days_is_deleted() {
                    // when
                    var changePeriodResult = decidedTrip.changePeriod(notOverlappedPeriod);

                    // then
                    List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                    assertThat(deletedDayIds.size()).isEqualTo(5);
                    assertThat(deletedDayIds).containsExactly(
                            day1.getId(), day2.getId(), day3.getId(),
                            day4.getId(), day5.getId()
                    );
                }

                @Test
                @DisplayName("새로운 기간의 Day들이 모두 생성된다.")
                public void newPeriod_days_are_all_created() {
                    // when
                    var changePeriodResult = decidedTrip.changePeriod(notOverlappedPeriod);

                    // then
                    List<Day> createdDays = changePeriodResult.getCreatedDays();
                    assertThat(createdDays).map(Day::getTripDate).containsExactly(
                            LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 7),
                            LocalDate.of(2023, 3, 8), LocalDate.of(2023, 3, 9),
                            LocalDate.of(2023, 3, 10));
                }

                @Test
                @DisplayName("생성된 Day들은 모두 색상을 가진다.")
                public void all_createdPeriod_days_have_color() {
                    // when
                    var changePeriodResult = decidedTrip.changePeriod(notOverlappedPeriod);

                    // then
                    List<Day> createdDays = changePeriodResult.getCreatedDays();
                    assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull);
                }

                @Test
                @DisplayName("Trip은 새로운 기간의 Day들만 가진다")
                public void trip_has_new_Days_only() {
                    // when
                    decidedTrip.changePeriod(notOverlappedPeriod);

                    // then
                    assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactly(
                            LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 7),
                            LocalDate.of(2023, 3, 8), LocalDate.of(2023, 3, 9),
                            LocalDate.of(2023, 3, 10));
                }


                @Test
                @DisplayName("Trip은 여전히 decided 상태이다.")
                public void trip_status_is_still_decided() {
                    // when
                    decidedTrip.changePeriod(notOverlappedPeriod);

                    // then
                    assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
                }

                @Test
                @DisplayName("Trip의 기간은 새로 지정한 기간으로 변한다")
                public void trip_period_is_changed_to_newPeriod() {
                    // when
                    decidedTrip.changePeriod(notOverlappedPeriod);

                    // then
                    assertThat(decidedTrip.getTripPeriod()).isEqualTo(notOverlappedPeriod);
                }
            }

            @Nested
            @DisplayName("겹치는 기간으로 변경 테스트")
            class OverlappedPeriodChangeTest {

                @Nested
                @DisplayName("뒤에서 겹치는 기간으로 수정하면")
                class If_Change_to_back_overlapped_period {

                    // given
                    TripPeriod backOverlappedPeriod = TripPeriod.of(LocalDate.of(2023, 3, 4), LocalDate.of(2023, 3, 7));

                    @Test
                    @DisplayName("앞의 겹치지 않는 Day들이 삭제된다")
                    public void not_Overlapped_days_are_deleted() {
                        var changePeriodResult = decidedTrip.changePeriod(backOverlappedPeriod);

                        // then
                        List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                        assertThat(deletedDayIds).containsExactly(
                                day1.getId(), day2.getId(), day3.getId()
                        );
                    }

                    @Test
                    @DisplayName("새로 추가된 날짜의 Day들이 생성된다.")
                    public void new_added_days_are_all_created() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(backOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).map(Day::getTripDate).containsExactly(LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 7));
                    }

                    @Test
                    @DisplayName("새로 생성된 Day들은 모두 색상을 가진다")
                    public void all_createdDays_have_color() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(backOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull);
                    }

                    @Test
                    @DisplayName("Trip은 새로 변경된 기간의 Day들만 가진다.")
                    public void trip_has_newPeriod_Days_only() {
                        // when
                        decidedTrip.changePeriod(backOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactly(
                                LocalDate.of(2023, 3, 4), LocalDate.of(2023, 3, 5),
                                LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 7));
                    }

                    @Test
                    @DisplayName("Trip은 여전히 decided 상태이다.")
                    public void trip_status_is_still_decided() {
                        // when
                        decidedTrip.changePeriod(backOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
                    }

                    @Test
                    @DisplayName("Trip의 기간은 새로 지정한 기간으로 변한다")
                    public void trip_period_is_changed_to_newPeriod() {
                        // when
                        decidedTrip.changePeriod(backOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getTripPeriod()).isEqualTo(backOverlappedPeriod);
                    }

                }

                @Nested
                @DisplayName("앞에서 겹치는 기간으로 수정하면")
                class If_Change_to_front_overlapped_period {
                    // given
                    TripPeriod frontOverlappedPeriod = TripPeriod.of(LocalDate.of(2023, 2, 27), LocalDate.of(2023, 3, 3));

                    @Test
                    @DisplayName("뒤의 겹치지 않는 Day들이 삭제된다")
                    public void not_Overlapped_days_are_deleted() {
                        var changePeriodResult = decidedTrip.changePeriod(frontOverlappedPeriod);

                        // then
                        List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                        assertThat(deletedDayIds).containsExactly(day4.getId(), day5.getId());
                    }

                    @Test
                    @DisplayName("새로 추가된 날짜의 Day들이 생성된다.")
                    public void new_added_days_are_all_created() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(frontOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).map(Day::getTripDate).containsExactly(
                                LocalDate.of(2023, 2, 27), LocalDate.of(2023, 2, 28));
                        assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull);
                    }

                    @Test
                    @DisplayName("새로 생성된 Day들은 모두 색상을 가진다.")
                    public void all_CreatedDays_have_color() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(frontOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull);
                    }

                    @Test
                    @DisplayName("Trip은 새로 변경된 기간의 Day들만 가진다.")
                    public void trip_has_newPeriod_Days_only() {
                        // when
                        decidedTrip.changePeriod(frontOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactlyInAnyOrder(
                                LocalDate.of(2023, 2, 27), LocalDate.of(2023, 2, 28),
                                LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2),
                                LocalDate.of(2023, 3, 3));
                    }

                    @Test
                    @DisplayName("Trip은 여전히 decided 상태이다.")
                    public void trip_status_is_still_decided() {
                        // when
                        decidedTrip.changePeriod(frontOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
                    }

                    @Test
                    @DisplayName("Trip의 기간은 새로 지정한 기간으로 변한다")
                    public void trip_period_is_changed_to_newPeriod() {
                        // when
                        decidedTrip.changePeriod(frontOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getTripPeriod()).isEqualTo(frontOverlappedPeriod);
                    }
                }

                @Nested
                @DisplayName("내부에 포함된 기간으로 수정하면")
                class If_Change_to_inner_overlapped_period {
                    // given
                    TripPeriod innerOverlappedPeriod = TripPeriod.of(LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 4));

                    @Test
                    @DisplayName("겹치지 않는 Day들이 삭제된다")
                    public void not_Overlapped_days_are_deleted() {
                        var changePeriodResult = decidedTrip.changePeriod(innerOverlappedPeriod);

                        // then
                        List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                        assertThat(deletedDayIds).containsExactlyInAnyOrder(day1.getId(), day5.getId());
                    }

                    @Test
                    @DisplayName("새로 추가된 날짜가 없다.(내부에 포함된 작은 기간으로 줄였으므로)")
                    public void no_day_created() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(innerOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).isEmpty();
                    }

                    @Test
                    @DisplayName("Trip은 새로 변경된 기간의 Day들만 가진다.")
                    public void trip_has_newPeriod_Days_only() {
                        // when
                        decidedTrip.changePeriod(innerOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactlyInAnyOrder(
                                LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 3),
                                LocalDate.of(2023, 3, 4));
                    }

                    @Test
                    @DisplayName("Trip은 여전히 decided 상태이다.")
                    public void trip_status_is_still_decided() {
                        // when
                        decidedTrip.changePeriod(innerOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
                    }

                    @Test
                    @DisplayName("Trip의 기간은 새로 지정한 기간으로 변한다")
                    public void trip_period_is_changed_to_newPeriod() {
                        // when
                        decidedTrip.changePeriod(innerOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getTripPeriod()).isEqualTo(innerOverlappedPeriod);
                    }
                }

                @Nested
                @DisplayName("기존 기간을 포함한 외부의 더 큰 기간으로 수정하면")
                class If_Change_to_outer_overlapped_period {
                    // given
                    TripPeriod outerOverlappedPeriod = TripPeriod.of(LocalDate.of(2023, 2, 27), LocalDate.of(2023, 3, 7));

                    @Test
                    @DisplayName("삭제되는 Day가 없다.(더 기존의 Day를 포함한 큰 기간으로 변경하므로)")
                    public void no_day_created() {
                        var changePeriodResult = decidedTrip.changePeriod(outerOverlappedPeriod);

                        // then
                        List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();
                        assertThat(deletedDayIds).isEmpty();
                    }

                    @Test
                    @DisplayName("새로 추가되는 날짜에 해당되는 Day들이 추가된다.")
                    public void new_added_days_are_all_created() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(outerOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).map(Day::getTripDate)
                                .containsExactly(
                                        LocalDate.of(2023, 2, 27), LocalDate.of(2023, 2, 28),
                                        LocalDate.of(2023, 3, 6), LocalDate.of(2023, 3, 7));
                    }

                    @Test
                    @DisplayName("새로 생성된 Day들은 모두 Color를 가진다")
                    public void all_CreatedDays_have_color() {
                        // when
                        var changePeriodResult = decidedTrip.changePeriod(outerOverlappedPeriod);

                        // then
                        List<Day> createdDays = changePeriodResult.getCreatedDays();
                        assertThat(createdDays).map(Day::getDayColor).allMatch(Objects::nonNull);
                    }

                    @Test
                    @DisplayName("Trip은 기존의 것을 포함하여 새로운 기간의 모든 Day들을 가진다.")
                    public void trip_has_newPeriod_All() {
                        // when
                        decidedTrip.changePeriod(outerOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getDays()).map(Day::getTripDate).containsExactlyInAnyOrder(
                                LocalDate.of(2023, 2, 27), LocalDate.of(2023, 2, 28),
                                LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2),
                                LocalDate.of(2023, 3, 3), LocalDate.of(2023, 3, 4),
                                LocalDate.of(2023, 3, 5), LocalDate.of(2023, 3, 6),
                                LocalDate.of(2023, 3, 7));
                    }

                    @Test
                    @DisplayName("Trip은 여전히 decided 상태이다.")
                    public void trip_status_is_still_decided() {
                        // when
                        decidedTrip.changePeriod(outerOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getStatus()).isSameAs(TripStatus.DECIDED);
                    }

                    @Test
                    @DisplayName("Trip의 기간은 새로 지정한 기간으로 변한다")
                    public void trip_period_is_changed_to_newPeriod() {
                        // when
                        decidedTrip.changePeriod(outerOverlappedPeriod);

                        // then
                        assertThat(decidedTrip.getTripPeriod()).isEqualTo(outerOverlappedPeriod);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("CreateSchedule 테스트")
    class CreateScheduleTest {

        @DisplayName("임시보관함에 일정을 새로 추가할 경우")
        @Nested
        class Case_TemporaryStorage {

            @DisplayName("인덱스가 최대 범위를 벗어나면, ScheduleIndexRangeException 발생")
            @Test
            public void when_new_index_range_is_over_max_index_value_then_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행 제목"), 1L);

                Schedule schedule1 = Schedule.builder()
                        .day(null)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                        .build();

                trip.getTemporaryStorage().add(schedule1); // 원래 이 방식을 통해 추가하는 것은 도메인 규칙에 어긋나지만 범위를 벗어나는 테스트를 하기 위함.

                // when & then
                assertThatThrownBy(() ->
                        trip.createSchedule(null, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523))))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("인덱스가 최대 범위를 벗어나지 않으면, 정상적으로 다음 순서의 Schedule 생성됨")
            @Test
            public void successTest() {
                Trip trip = Trip.create(TripTitle.of("여행 제목"), 1L);

                Schedule schedule1 = trip.createSchedule(null, ScheduleTitle.of("일정 제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(null, ScheduleTitle.of("일정 제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));

                // 생성된 일정시간들은 디폴트 시간
                assertThat(schedule1.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
                assertThat(schedule2.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
            }
        }

        @DisplayName("특정 Day에 일정을 새로 추가할 경우")
        @Nested
        class Case_DaySchedule {

            @DisplayName("소속된 Day가 아닌 곳에 Day를 생성하라고 요청할 경우 InvalidTripDayException 발생")
            @Test
            public void when_trip_not_contains_day_then_it_throws_InvalidTripDayException() {
                // given
                Trip trip = Trip.builder()
                        .id(1L)
                        .tripperId(1L)
                        .tripTitle(TripTitle.of("여행 제목1"))
                        .status(TripStatus.DECIDED)
                        .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                        .build();

                Day validDay = Day.builder()
                        .id(1L)
                        .tripDate(LocalDate.of(2023, 3, 1))
                        .trip(trip)
                        .build();

                trip.getDays().add(validDay);

                Trip otherTrip = Trip.builder()
                        .id(2L)
                        .tripperId(2L)
                        .tripTitle(TripTitle.of("여행제목2"))
                        .status(TripStatus.DECIDED)
                        .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 2), LocalDate.of(2023, 3, 2)))
                        .build();
                Day otherTripDay = Day.builder()
                        .id(2L)
                        .tripDate(LocalDate.of(2023, 3, 2))
                        .trip(otherTrip)
                        .build();

                otherTrip.getDays().add(otherTripDay);

                // when & then
                assertThatThrownBy(() -> trip.createSchedule(otherTripDay, ScheduleTitle.of("일정 제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523))))
                        .isInstanceOf(InvalidTripDayException.class);
            }

            @DisplayName("인덱스가 최대 범위를 벗어나면, ScheduleIndexRangeException 발생")
            @Test
            public void when_new_index_range_is_over_max_index_value_then_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.builder()
                        .id(1L)
                        .tripperId(1L)
                        .tripTitle(TripTitle.of("여행 제목1"))
                        .status(TripStatus.DECIDED)
                        .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                        .build();
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));


                Day day = Day.builder()
                        .id(1L)
                        .tripDate(LocalDate.of(2023, 3, 1))
                        .trip(trip)
                        .build();

                trip.getDays().add(day);


                Schedule schedule1 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                        .build();

                day.getSchedules().add(schedule1); // 원래 이 방식을 통해 추가하는 것은 도메인 규칙에 어긋나지만 범위를 벗어나는 테스트를 하기 위함.

                // when & then
                assertThatThrownBy(() ->
                        trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523))))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("인덱스가 최대 범위를 벗어나지 않으면, 정상적으로 다음 순서의 Schedule 생성됨")
            @Test
            public void successTest() {
                Trip trip = Trip.builder()
                        .id(1L)
                        .tripperId(1L)
                        .tripTitle(TripTitle.of("여행 제목1"))
                        .status(TripStatus.DECIDED)
                        .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                        .build();

                Day day = Day.builder()
                        .id(1L)
                        .tripDate(LocalDate.of(2023, 3, 1))
                        .trip(trip)
                        .build();

                trip.getDays().add(day);

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정 제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정 제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));

                // 생성된 일정시간들은 디폴트 시간
                assertThat(schedule1.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
                assertThat(schedule2.getScheduleTime()).isEqualTo(ScheduleTime.defaultTime());
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
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;
                Schedule schedule = trip.createSchedule(day, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, day, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 임시보관함 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_temporary_storage_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, day, 3))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("임시보관함 내에서, 자신의 기존 순서로 이동할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_same_position_then_nothing_changed() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                Schedule firstSchedule = temporaryStorage.get(0);
                Schedule secondSchedule = temporaryStorage.get(1);

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }

            @DisplayName("임시보관함 내에 기존의 순서 다음으로 이동시키려 할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_after_currentOrder_then_nothing_changed() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 2);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                Schedule firstSchedule = temporaryStorage.get(0);
                Schedule secondSchedule = temporaryStorage.get(1);

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }

            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_TemporaryStorageSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, day, 2);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_TemporaryStorageSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                trip.getTemporaryStorage().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, day, 2))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞으로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_Safe_then_schedule_move_to_Head() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 0);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(-ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE))
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                trip.getTemporaryStorage().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule2, day, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule3 = trip.createSchedule(day, ScheduleTitle.of("일정제목3"), Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, day, 2);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                assertThat(temporaryStorage.size()).isEqualTo(3);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                Day day = null;

                Schedule schedule1 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(10))
                        .build();

                Schedule schedule3 = Schedule.builder()
                        .day(day)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목3"))
                        .place(Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(11))
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                trip.getTemporaryStorage().add(schedule2);
                trip.getTemporaryStorage().add(schedule3);


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
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);

                Trip otherTrip = Trip.create(TripTitle.of("다른 여행 제목"), 1L);
                otherTrip.changePeriod(TripPeriod.of(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 1)));

                Day beforeDay = null;
                Schedule schedule = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                Day targetDay = otherTrip.getDays().get(0);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, 0))
                        .isInstanceOf(InvalidTripDayException.class);
            }

            @DisplayName("targetOrder가 0보다 작으면 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_under_zero_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                // given
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));
                Day beforeDay = null;
                Schedule schedule = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                Day targetDay = trip.getDays().get(0);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 Schedules 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_day_schedules_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                Day targetDay = trip.getDays().get(0);
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 2))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }


            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Day targetDay = trip.getDays().get(0);

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(2);
                assertThat(schedules).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Day targetDay = trip.getDays().get(0);

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                targetDay.getSchedules().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_isSafe_schedule_move_to_Head() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Day targetDay = trip.getDays().get(0);

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 0);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(2);
                assertThat(schedules).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(-ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Day targetDay = trip.getDays().get(0);

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE))
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                targetDay.getSchedules().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Day targetDay = trip.getDays().get(0);

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule3 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목3"), Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(3);
                assertThat(schedules).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(schedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = null;
                Day targetDay = trip.getDays().get(0);

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(10))
                        .build();

                Schedule schedule3 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목3"))
                        .place(Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(11))
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                targetDay.getSchedules().add(schedule2);
                targetDay.getSchedules().add(schedule3);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
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
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));
                Day beforeDay = trip.getDays().get(0);
                Schedule schedule = trip.createSchedule(beforeDay, ScheduleTitle.of("여행 제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                Trip otherTrip = Trip.create(TripTitle.of("다른 여행 제목"), 1L);
                otherTrip.changePeriod(TripPeriod.of(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 1)));
                Day targetDay = otherTrip.getDays().get(0);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, 0))
                        .isInstanceOf(InvalidTripDayException.class);
            }

            @DisplayName("targetOrder가 0보다 작으면 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_under_zero_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                // given
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));
                Day beforeDay = trip.getDays().get(0);
                Schedule schedule = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                Day targetDay = trip.getDays().get(1);

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 Schedules 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_day_schedules_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));


                Day targetDay = trip.getDays().get(1);
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 2))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("같은 Day의 기존의 순서로 이동할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_same_day_and_same_position_then_nothing_changed() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));
                Day day = trip.getDays().get(0);

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 1);

                // then
                List<Schedule> schedules = day.getSchedules();
                Schedule firstSchedule = schedules.get(0);
                Schedule secondSchedule = schedules.get(1);

                assertThat(schedules.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }

            @DisplayName("같은 Day의 기존의 순서 다음으로 이동시키려 할 경우, 아무런 변화도 일어나지 않는다.")
            @Test
            public void when_move_to_same_day_and_after_currentOrder_then_nothing_changed() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));
                Day day = trip.getDays().get(0);

                Schedule schedule1 = trip.createSchedule(day, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(day, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule2, day, 2);

                // then
                List<Schedule> schedules = day.getSchedules();
                Schedule firstSchedule = schedules.get(0);
                Schedule secondSchedule = schedules.get(1);

                assertThat(schedules.size()).isEqualTo(2);
                assertThat(firstSchedule).isEqualTo(schedule1);
                assertThat(firstSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(secondSchedule).isEqualTo(schedule2);
                assertThat(secondSchedule.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(day.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(false);
            }


            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> targetDaySchedules = targetDay.getSchedules();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(targetDaySchedules.size()).isEqualTo(2);
                assertThat(targetDaySchedules).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 Schedules 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_SchedulesSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                        .build();

                beforeDay.getSchedules().add(schedule1);
                targetDay.getSchedules().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞으로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_isSafe_schedule_move_to_Head() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 0);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> targetDaySchedules = targetDay.getSchedules();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(targetDaySchedules.size()).isEqualTo(2);
                assertThat(targetDaySchedules).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(-ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE))
                        .build();

                beforeDay.getSchedules().add(schedule1);
                targetDay.getSchedules().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule3 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목3"), Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();
                List<Schedule> schedules = targetDay.getSchedules();

                assertThat(temporaryStorage).isEmpty();
                assertThat(schedules.size()).isEqualTo(3);
                assertThat(schedules).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(schedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(targetDay.getId());
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = trip.getDays().get(1);

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(10))
                        .build();

                Schedule schedule3 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목3"))
                        .place(Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(11))
                        .build();

                trip.getTemporaryStorage().add(schedule1);
                targetDay.getSchedules().add(schedule2);
                targetDay.getSchedules().add(schedule3);


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
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));
                Day beforeDay = trip.getDays().get(0);
                Schedule schedule = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));

                Day targetDay = null;

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule, targetDay, -1))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }

            @DisplayName("targetOrder가 임시보관함 크기를 넘어가는 경우 InvalidScheduleMoveTargetOrderException 발생")
            @Test
            public void when_targetOrder_is_over_temporary_storage_max_size_then_it_throws_InvalidScheduleMoveTargetOrderException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));
                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 2))
                        .isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
            }


            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하면 맨 뒤로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_temporaryStorageSize_and_tail_scheduleIndex_isSafe_schedule_move_to_Tail() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 임시보관함 크기와 똑같은 값이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_temporaryStorageSize_and_tail_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                        .build();

                beforeDay.getSchedules().add(schedule1);
                trip.getTemporaryStorage().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }


            @DisplayName("targetOrder가 0이고, 맨 앞 ScheduleIndex 범위가 안전하면 맨 앞으로 이동한다.")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_Head_scheduleIndex_isSafe_schedule_move_to_Head() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 21L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 0);

                // then
                List<Schedule> beforeDaySchedules = beforeDay.getSchedules();
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(beforeDaySchedules).isEmpty();
                assertThat(temporaryStorage.size()).isEqualTo(2);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(ScheduleIndex.of(-ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 0이고, 끝 ScheduleIndex 범위가 안전하지 않으면 ScheduleIndexRangeException 발생")
            @Test
            public void when_targetOrder_isEqualTo_Zero_and_Head_scheduleIndex_is_unSafe_it_throws_ScheduleIndexRangeException() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 2)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE))
                        .build();

                beforeDay.getSchedules().add(schedule1);
                trip.getTemporaryStorage().add(schedule2);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 0))
                        .isInstanceOf(ScheduleIndexRangeException.class);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하면 중간 인덱스가 부여된다.")
            @Test
            public void testMiddleInsert_Success() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;

                Schedule schedule1 = trip.createSchedule(beforeDay, ScheduleTitle.of("일정제목1"), Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule2 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목2"), Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)));
                Schedule schedule3 = trip.createSchedule(targetDay, ScheduleTitle.of("일정제목3"), Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)));

                // when
                ScheduleMoveDto scheduleMoveDto = trip.moveSchedule(schedule1, targetDay, 1);

                // then
                List<Schedule> schedules = beforeDay.getSchedules();
                List<Schedule> temporaryStorage = trip.getTemporaryStorage();

                assertThat(schedules).isEmpty();
                assertThat(temporaryStorage.size()).isEqualTo(3);
                assertThat(temporaryStorage).containsExactlyInAnyOrder(schedule1, schedule2, schedule3);
                assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
                assertThat(schedule2.getScheduleIndex()).isEqualTo(ScheduleIndex.ZERO_INDEX);
                assertThat(schedule3.getScheduleIndex()).isEqualTo(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP));
                assertThat(scheduleMoveDto.getBeforeDayId()).isEqualTo(beforeDay.getId());
                assertThat(scheduleMoveDto.getAfterDayId()).isEqualTo(null);
                assertThat(scheduleMoveDto.isPositionChanged()).isEqualTo(true);
            }

            @DisplayName("targetOrder가 다른 일정의 순서이고, 해당 순서 앞과 간격이 충분하지 않으면 MidScheduleIndexConflictException 발생")
            @Test
            public void testMiddleInsert_Failure() {
                Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
                trip.changePeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)));

                Day beforeDay = trip.getDays().get(0);
                Day targetDay = null;


                Schedule schedule1 = Schedule.builder()
                        .day(beforeDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목1"))
                        .place(Place.of("place-id111", "place 이름111", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                        .build();

                Schedule schedule2 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목2"))
                        .place(Place.of("place-id222", "place 이름222", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(10))
                        .build();

                Schedule schedule3 = Schedule.builder()
                        .day(targetDay)
                        .trip(trip)
                        .scheduleTitle(ScheduleTitle.of("일정제목3"))
                        .place(Place.of("place-id333", "place 이름333", Coordinate.of(37.72221, 137.86523)))
                        .scheduleIndex(ScheduleIndex.of(11))
                        .build();

                beforeDay.getSchedules().add(schedule1);
                trip.getTemporaryStorage().add(schedule2);
                trip.getTemporaryStorage().add(schedule3);


                // when & then
                assertThatThrownBy(() -> trip.moveSchedule(schedule1, targetDay, 1))
                        .isInstanceOf(MidScheduleIndexConflictException.class);
            }
        }
    }
}
