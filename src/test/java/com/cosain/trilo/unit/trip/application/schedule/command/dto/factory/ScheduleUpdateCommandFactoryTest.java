package com.cosain.trilo.unit.trip.application.schedule.command.dto.factory;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory.ScheduleUpdateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleTitleException;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("ScheduleUpdateCommandFactory 테스트")
public class ScheduleUpdateCommandFactoryTest {

    private ScheduleUpdateCommandFactory scheduleUpdateCommandFactory = new ScheduleUpdateCommandFactory();


    @DisplayName("제목 null -> 검증 예외 발생")
    @Test
    void nullScheduleTitleTest() {
        // given
        String rawScheduleTitle = null;
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTitleException.class);
    }

    @DisplayName("제목 빈 문자열 -> 검증 예외 발생")
    @Test
    void emptyScheduleTitleTest() {
        // given
        String rawScheduleTitle = "";
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTitleException.class);
    }

    @DisplayName("제목 공백으로만 구성된 문자열 -> 검증 예외 발생")
    @Test
    void whiteSpaceScheduleTitleTest() {
        // given
        String rawScheduleTitle = "    ";
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTitleException.class);
    }

    @DisplayName("제목이 제한 글자수보다 긴 문자열 -> 검증 예외 발생")
    @Test
    void tooLongScheduleTitleTest() {
        // given
        String rawScheduleTitle = "가".repeat(ScheduleTitle.MAX_LENGTH + 1);
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
    }

}
