package com.cosain.trilo.unit.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

}
