package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.common.exception.trip.InvalidTripTitleException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TripTitle 테스트")
public class TripTitleTest {

    @Test
    @DisplayName("null 제목 -> InvalidTripTitleException 발생")
    void nullTripTitleTest() {
        // given
        String nullTitle = null;

        // when
        assertThatThrownBy(() -> TripTitle.of(nullTitle))
                .isInstanceOf(InvalidTripTitleException.class);
    }

    @Test
    @DisplayName("빈 문자열 제목 -> InvalidTripTitleException 발생")
    void emptyTripTitleTest() {
        // given
        String emptyTitle = "";

        // when
        assertThatThrownBy(() -> TripTitle.of(emptyTitle))
                .isInstanceOf(InvalidTripTitleException.class);
    }

    @Test
    @DisplayName("공백만으로 구성된 제목 -> InvalidTripTitleException 발생")
    void whiteSpaceTripTitleTest() {
        // given
        String whiteSpaceTitle = "   ";

        // when
        assertThatThrownBy(() -> TripTitle.of(whiteSpaceTitle))
                .isInstanceOf(InvalidTripTitleException.class);
    }

    @Test
    @DisplayName("제한 길이보다 긴 제목 -> InvalidTripTitleException 발생")
    void tooLongTripTitleTest() {
        // given
        String tooLongTitle = "A".repeat(TripTitle.MAX_LENGTH + 1);

        // when
        assertThatThrownBy(() -> TripTitle.of(tooLongTitle))
                .isInstanceOf(InvalidTripTitleException.class);
    }

    @Test
    @DisplayName("정상적인 길이의 비어있지 않은 여행제목 -> 생성 성공")
    void createTripTitleSuccessTest() {
        // given
        String normalTitle = "정상적인 제목";

        // when
        TripTitle title = TripTitle.of(normalTitle);

        // then
        assertThat(title.getValue()).isEqualTo(normalTitle);
    }

    @Test
    @DisplayName("같은 제목일 때 동등하다")
    void equalsTest() {
        // given
        String normalTitle = "정상적인 제목";
        TripTitle title1 = TripTitle.of(normalTitle);
        TripTitle title2 = TripTitle.of(normalTitle);

        // when
        boolean equality = title1.equals(title2);

        // then
        assertTrue(equality);
    }
}
