package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.common.exception.schedule.InvalidScheduleTitleException;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ScheduleTitle 테스트")
public class ScheduleTitleTest {

    @DisplayName("일정 제목이 null 아니고 20자 이하(공백 허용) -> 정상 생성")
    @ValueSource(strings = {"일정 제목", "", "     "})
    @ParameterizedTest
    void successCreateTest(String rawTitle) {
        // given : rawTitle

        // when
        ScheduleTitle title = ScheduleTitle.of(rawTitle);

        // then
        assertThat(title.getValue()).isEqualTo(rawTitle);
    }

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
    @DisplayName("35자보다 긴 제목 -> InvalidScheduleTitleException 발생")
    void tooLongScheduleTitleTest() {
        // given
        String tooLongTitle = "A".repeat(36);

        // when
        assertThatThrownBy(() -> ScheduleTitle.of(tooLongTitle))
                .isInstanceOf(InvalidScheduleTitleException.class);
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
