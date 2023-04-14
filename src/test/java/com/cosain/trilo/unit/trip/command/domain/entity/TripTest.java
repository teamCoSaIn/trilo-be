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
    @DisplayName("changeTitle로 제목을 변경할 때")
    class When_ChangeTitle_Method {

        @Nested
        @DisplayName("같은 제목으로 변경하면")
        class Change_To_Same_Title {
            // given
            private final Trip trip = Trip.create("제목", 1L);
            private final String sameTitle = "제목";

            @Test
            @DisplayName("제목이 변경되지도 않고, false가 반환된다.")
            void it_returns_false_and_title_no_changed() {
                // when
                boolean changed = trip.changeTitle(sameTitle);

                // then
                assertThat(changed).isFalse();
                assertThat(trip.getTitle()).isEqualTo("제목");
            }

        }

        @Nested
        @DisplayName("다른 제목으로 변경하면")
        class Change_To_Different_Title {
            // given
            private final Trip trip = Trip.create("제목", 1L);
            private final String newTitle = "바뀐 제목";

            @Test
            @DisplayName("제목이 변경되고, true가 반환된다.")
            void it_returns_true_and_title_changed() {
                // when
                boolean changed = trip.changeTitle(newTitle);

                // then
                assertThat(changed).isTrue();
                assertThat(trip.getTitle()).isEqualTo("바뀐 제목");
            }

        }
    }

}
