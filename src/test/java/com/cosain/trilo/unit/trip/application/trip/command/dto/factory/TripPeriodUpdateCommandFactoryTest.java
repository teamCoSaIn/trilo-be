package com.cosain.trilo.unit.trip.application.trip.command.dto.factory;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.factory.TripPeriodUpdateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidPeriodException;
import com.cosain.trilo.trip.domain.exception.TooLongPeriodException;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@Slf4j
@DisplayName("TripPeriodUpdateCommandFactory 테스트")
public class TripPeriodUpdateCommandFactoryTest {

    private TripPeriodUpdateCommandFactory tripPeriodUpdateCommandFactory;

    @BeforeEach
    void setUp() {
        this.tripPeriodUpdateCommandFactory = new TripPeriodUpdateCommandFactory();
    }


    @DisplayName("시작일만 Null -> 검증예외 발생")
    @Test
    public void onlyStartDateNullTest() {
        // given
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023,3,2);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripPeriodUpdateCommandFactory.createCommand(startDate, endDate),
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
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripPeriodUpdateCommandFactory.createCommand(startDate, endDate),
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
        LocalDate startDate = LocalDate.of(2023,3,2);
        LocalDate endDate = LocalDate.of(2023,3,1);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripPeriodUpdateCommandFactory.createCommand(startDate, endDate),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidPeriodException.class);
    }

    @DisplayName("날짜가 모두 null -> 정상 생성")
    @Test
    public void bothDateNullTest() {
        LocalDate startDate = null;
        LocalDate endDate = null;

        // when
        TripPeriodUpdateCommand command = tripPeriodUpdateCommandFactory.createCommand(startDate, endDate);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getTripPeriod()).isEqualTo(TripPeriod.of(startDate, endDate));
    }

    @DisplayName("여행 일수가 딱 10일 -> 정상 생성")
    @Test
    public void periodLength_is_ten_dates() {
        // given
        LocalDate startDate = LocalDate.of(2023,5,1);
        LocalDate endDate = LocalDate.of(2023,5,10);

        // when
        TripPeriodUpdateCommand command = tripPeriodUpdateCommandFactory.createCommand(startDate, endDate);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getTripPeriod()).isEqualTo(TripPeriod.of(startDate, endDate));
    }

    @DisplayName("여행 일수가 10일 초과 -> 검증 예외 발생")
    @Test
    public void tooLongTripPeriodTest() {
        // given
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,11);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> tripPeriodUpdateCommandFactory.createCommand(startDate, endDate),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(TooLongPeriodException.class);
    }

}
