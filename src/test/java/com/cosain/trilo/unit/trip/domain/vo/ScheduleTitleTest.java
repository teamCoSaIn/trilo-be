package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleTitleException;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ScheduleTitle 테스트")
public class ScheduleTitleTest {

    @Test
    @DisplayName("null 제목 -> InvalidScheduleTitleException 발생")
    void nullScheduleTitleTest() {
        // given
        String nullTitle = null;

        // when
        assertThatThrownBy(() -> ScheduleTitle.of(nullTitle))
                .isInstanceOf(InvalidScheduleTitleException.class);
    }

    @Test
    @DisplayName("빈 문자열 제목 -> InvalidScheduleTitleException 발생")
    void emptyScheduleTitleTest() {
        // given
        String emptyTitle = "";

        // when
        assertThatThrownBy(() -> ScheduleTitle.of(emptyTitle))
                .isInstanceOf(InvalidScheduleTitleException.class);
    }

    @Test
    @DisplayName("공백만으로 구성된 제목 -> InvalidScheduleTitleException 발생")
    void whiteSpaceScheduleTitleTest() {
        // given
        String whiteSpaceTitle = "   ";

        // when
        assertThatThrownBy(() -> ScheduleTitle.of(whiteSpaceTitle))
                .isInstanceOf(InvalidScheduleTitleException.class);
    }

    @Test
    @DisplayName("제한 길이보다 긴 제목 -> InvalidScheduleTitleException 발생")
    void tooLongScheduleTitleTest() {
        // given
        String tooLongTitle = "A".repeat(ScheduleTitle.MAX_LENGTH + 1);

        // when
        assertThatThrownBy(() -> ScheduleTitle.of(tooLongTitle))
                .isInstanceOf(InvalidScheduleTitleException.class);
    }

    @Test
    @DisplayName("정상적인 길이의 비어있지 않은 여행제목 -> 생성 성공")
    void createScheduleTitleSuccessTest() {
        // given
        String normalTitle = "정상적인 제목";

        // when
        ScheduleTitle title = ScheduleTitle.of(normalTitle);

        // then
        assertThat(title.getValue()).isEqualTo(normalTitle);
    }

    @Test
    @DisplayName("같은 제목일 때 동등하다")
    void equalsTest() {
        // given
        String normalTitle = "정상적인 제목";
        ScheduleTitle title1 = ScheduleTitle.of(normalTitle);
        ScheduleTitle title2 = ScheduleTitle.of(normalTitle);

        // when
        boolean equality = title1.equals(title2);

        // then
        assertTrue(equality);
    }
}
