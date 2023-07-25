package com.cosain.trilo.unit.trip.application.trip.service.trip_create;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateCommand;
import com.cosain.trilo.common.exception.trip.InvalidTripTitleException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@Slf4j
@DisplayName("TripCreateCommand 테스트")
public class TripCreateCommandTest {

    @DisplayName("제목이 올바른 길이 -> 정상 생성")
    @Test
    public void createSuccessTest() {
        // given
        long tripperId = 1L;
        String rawTitle = "제목";

        // when
        TripCreateCommand createCommand = TripCreateCommand.of(tripperId, rawTitle);

        // then
        assertThat(createCommand).isNotNull();
        assertThat(createCommand.getTripperId()).isEqualTo(tripperId);
        assertThat(createCommand.getTripTitle()).isEqualTo(TripTitle.of(rawTitle));
    }

    @DisplayName("제목 null -> 검증예외 발생")
    @Test
    public void testNullTitle() {
        // given
        long tripperId = 1L;
        String nullTitle = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> TripCreateCommand.of(tripperId, nullTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("제목 빈문자열 -> 검증예외 발생")
    @Test
    public void emptyTitle() {
        // given
        long tripperId = 1L;
        String emptyTitle = "";

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> TripCreateCommand.of(tripperId, emptyTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("제목 공백으로만 구성 -> 검증예외 발생")
    @Test
    public void whiteSpaceTitle() {
        // given
        long tripperId = 1L;
        String whiteSpaceTitle = "     ";

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> TripCreateCommand.of(tripperId, whiteSpaceTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("20자보다 긴 제목 -> 검증예외 발생")
    @Test
    public void tooLongTitle() {
        // given
        long tripperId = 1L;
        String tooLongTitle = "가".repeat(21);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> TripCreateCommand.of(tripperId, tooLongTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

}
