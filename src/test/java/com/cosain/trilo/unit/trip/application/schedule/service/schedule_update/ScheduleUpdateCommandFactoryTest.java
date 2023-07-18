package com.cosain.trilo.unit.trip.application.schedule.service.schedule_update;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.schedule.service.schedule_update.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_update.ScheduleUpdateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleContentException;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleTimeException;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleTitleException;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("ScheduleUpdateCommandFactory 테스트")
public class ScheduleUpdateCommandFactoryTest {

    private ScheduleUpdateCommandFactory scheduleUpdateCommandFactory = new ScheduleUpdateCommandFactory();

    @DisplayName("일정 제목이 null 아니고 20자 이하(공백 허용) -> 정상 생성")
    @ValueSource(strings = {"일정 제목", "", "     "})
    @ParameterizedTest
    void scheduleTitleSuccessTest(String rawScheduleTitle) {
        // given
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        ScheduleUpdateCommand scheduleUpdateCommand = scheduleUpdateCommandFactory.createCommand(
                rawScheduleTitle, rawScheduleContent, startTime, endTime);

        // then
        assertThat(scheduleUpdateCommand.getScheduleTitle()).isEqualTo(ScheduleTitle.of(rawScheduleTitle));
        assertThat(scheduleUpdateCommand.getScheduleContent()).isEqualTo(ScheduleContent.of(rawScheduleContent));
        assertThat(scheduleUpdateCommand.getScheduleTime()).isEqualTo(ScheduleTime.of(startTime, endTime));
    }

    @DisplayName("제목이 20자보다 긴 문자열 -> 검증 예외 발생")
    @Test
    void tooLongScheduleTitleTest() {
        // given
        String rawScheduleTitle = "가".repeat(21);
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

    @DisplayName("일정의 본문이 null -> 검증 예외 발생")
    @Test
    void nullContentTest() {
        // given
        String rawScheduleTitle = "일정 제목";
        String rawScheduleContent = null;
        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleContentException.class);
    }

    @DisplayName("일정본문 65535 바이트 초과 -> 검증 예외 발생")
    @ValueSource(ints = {65536, 65537, 12345678})
    @ParameterizedTest
    void largeContentTest(int size) {
        // given
        String rawScheduleTitle = "일정 제목";

        byte[] bytes = new byte[size];
        Arrays.fill(bytes, (byte) 'A');
        String rawScheduleContent = new String(bytes, StandardCharsets.UTF_8);

        LocalTime startTime = LocalTime.of(13,0);
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleContentException.class);
    }

    @DisplayName("일정의 시작 시점이 null -> 검증 예외 발생")
    @Test
    void startTimeNullTest() {
        // given
        String rawScheduleTitle = "일정 제목";
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = null;
        LocalTime endTime = LocalTime.of(13,5);

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTimeException.class);
    }

    @DisplayName("일정의 종료 시점이 null -> 검증 예외 발생")
    @Test
    void endTimeNullTest() {
        // given
        String rawScheduleTitle = "일정 제목";
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = LocalTime.of(13,5);
        LocalTime endTime = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTimeException.class);
    }

    @DisplayName("일정 시간 둘다 null -> 검증 예외 발생")
    @Test
    void bothTimeNullTest() {
        // given
        String rawScheduleTitle = "일정 제목";
        String rawScheduleContent = "일정 본문";
        LocalTime startTime = null;
        LocalTime endTime = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleUpdateCommandFactory.createCommand(rawScheduleTitle, rawScheduleContent, startTime, endTime),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTimeException.class);
    }

}
