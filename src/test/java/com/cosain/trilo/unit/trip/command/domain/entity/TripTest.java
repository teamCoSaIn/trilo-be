package com.cosain.trilo.unit.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.cosain.trilo.fixture.TripFixture.DECIDED_TRIP;
import static com.cosain.trilo.fixture.TripFixture.UNDECIDED_TRIP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[TripCommand] Trip 테스트")
public class TripTest {

    @Nested
    @DisplayName("여행을 create로 생성하면")
    class When_Create {

        // given
        private final String title = "제목";
        private final Long tripperId = 1L;

        @Test
        @DisplayName("지정한 이름을 가진 여행이 생성된다.")
        public void createdTrip_has_same_title() {
            // when
            Trip trip = Trip.create(title, tripperId);

            // then
            assertThat(trip.getTitle()).isEqualTo("제목");
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
            String beforeTitle = "변경 전 제목";
            Long tripperId = 1L;
            Trip trip = Trip.create(beforeTitle, tripperId);

            // when
            String newTitle = "변경 후 제목";
            trip.changeTitle(newTitle);

            // then
            assertThat(trip.getTitle()).isEqualTo("변경 후 제목");
        }

    }

    @Nested
    @DisplayName("'getNotOverlappedDays 로 겹치지 않는 날짜를 가져올 때")
    class When_getNotOverlappedDays {

        @Nested
        @DisplayName("Trip 상태가 UNDECIDED일 경우")
        class if_trip_status_is_undecided {

            @Test
            @DisplayName("빈 리스트를 반환한다.(겹치지 않는 날짜가 없으므로)")
            public void it_returns_empty_list() {
                // given
                Trip trip = UNDECIDED_TRIP.create(1L, 1L, "제목");
                // when
                List<Day> notOverlappedDays = trip.getNotOverlappedDays(LocalDate.of(2023, 5, 5), LocalDate.of(2023, 5, 10));
                // then
                assertThat(notOverlappedDays).isEmpty();
            }

            @Test
            public void 겹치지_않는_Day_리스트_가져올_때_DECIDED_상태인_경우() {
                // given
                Trip trip = DECIDED_TRIP.create(1L, 1L, "제목", LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 13));
                // when
                List<Day> notOverlappedDays = trip.getNotOverlappedDays(LocalDate.of(2023, 5, 12), LocalDate.of(2023, 5, 14));
                // then
                assertThat(notOverlappedDays.size()).isEqualTo(2);
                assertThat(notOverlappedDays.get(0).getTripDate()).isEqualTo(LocalDate.of(2023, 5, 10));
                assertThat(notOverlappedDays.get(notOverlappedDays.size() - 1).getTripDate()).isEqualTo(LocalDate.of(2023, 5, 11));
            }
        }
    }

}
