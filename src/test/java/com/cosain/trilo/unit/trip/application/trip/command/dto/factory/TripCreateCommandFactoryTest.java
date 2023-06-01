package com.cosain.trilo.unit.trip.application.trip.command.dto.factory;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripCreateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidTripTitleException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@Slf4j
@DisplayName("TripCreateCommandFactory 테스트")
public class TripCreateCommandFactoryTest {

    private TripCreateCommandFactory tripCreateCommandFactory = new TripCreateCommandFactory();

    @DisplayName("제목 null -> 검증예외 발생")
    @Test
    public void testNullTitle() {
        // given
        String nullTitle = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripCreateCommandFactory.createCommand(nullTitle),
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
        String emptyTitle = "";

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripCreateCommandFactory.createCommand(emptyTitle),
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
        String whiteSpaceTitle = "    ";

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripCreateCommandFactory.createCommand(whiteSpaceTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("제한보다 긴 제목 -> 검증예외 발생")
    @Test
    public void tooLongTitle() {
        // given
        String tooLongTitle = "가".repeat(TripTitle.MAX_LENGTH + 1);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripCreateCommandFactory.createCommand(tooLongTitle),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }

    @DisplayName("제목이 올바른 길이 -> 정상 생성")
    @Test
    public void createSuccessTest() {
        // given
        String normalTitle = "제목";

        // when
        TripCreateCommand createCommand = tripCreateCommandFactory.createCommand(normalTitle);

        // then
        assertThat(createCommand).isNotNull();
        assertThat(createCommand.getTripTitle()).isEqualTo(TripTitle.of(normalTitle));
    }
}
