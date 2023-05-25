package com.cosain.trilo.unit.trip.application.trip.command.dto.factory;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripUpdateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidPeriodException;
import com.cosain.trilo.trip.domain.exception.InvalidTripTitleException;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@Slf4j
@DisplayName("TripUpdateCommandFactory 테스트")
public class TripUpdateCommandFactoryTest {

    private TripUpdateCommandFactory tripUpdateCommandFactory = new TripUpdateCommandFactory();

    @DisplayName("제목 null -> 검증예외 발생")
    @Test
    public void testNullTitle() {
        // given
        String nullTitle = null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(nullTitle, startDate, endDate),
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
        LocalDate startDate = null;
        LocalDate endDate = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(emptyTitle, startDate, endDate),
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
        LocalDate startDate = null;
        LocalDate endDate = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(whiteSpaceTitle, startDate, endDate),
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
        LocalDate startDate = null;
        LocalDate endDate = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(tooLongTitle, startDate, endDate),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidTripTitleException.class);
    }


    @DisplayName("시작일만 Null -> 검증예외 발생")
    @Test
    public void onlyStartDateNullTest() {
        // given
        String title = "제목";
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023,3,1);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(title, startDate, endDate),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidPeriodException.class);
    }

    @DisplayName("종료일만 Null -> 검증예외 발생")
    @Test
    public void onlyEndDateNullTest() {
        // given
        String title = "제목";
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023,3,1);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(title, startDate, endDate),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidPeriodException.class);
    }

    @DisplayName("종료일이 시작일보다 앞설 때 -> 검증예외 발생")
    @Test
    public void fastEndDateTest() {
        // given
        String title = "제목";
        LocalDate startDate = LocalDate.of(2023,3,2);
        LocalDate endDate = LocalDate.of(2023,3,1);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripUpdateCommandFactory.createCommand(title, startDate, endDate),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidPeriodException.class);
    }

    @DisplayName("제목이 올바른 길이이고 날짜가 모두 null -> 정상 생성")
    @Test
    public void createSuccessTest1() {
        // given
        String normalTitle = "제목";
        LocalDate startDate = null;
        LocalDate endDate = null;

        // when
        TripUpdateCommand command = tripUpdateCommandFactory.createCommand(normalTitle, startDate, endDate);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getTripTitle()).isEqualTo(TripTitle.of(normalTitle));
        assertThat(command.getTripPeriod()).isEqualTo(TripPeriod.of(startDate, endDate));
    }
}
