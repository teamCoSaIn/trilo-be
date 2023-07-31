package com.cosain.trilo.unit.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.common.exception.schedule.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("ScheduleMoveCommand 테스트")
public class ScheduleMoveCommandTest {

    @DisplayName("targetOrder가 음이 아닌 정수 -> 성공")
    @Test
    public void successTest() {
        // given
        long scheduleId = 1L;
        long requestTripperId = 2L;
        Long targetDayId = 1L;
        Integer targetOrder = 0;

        // when
        ScheduleMoveCommand command = ScheduleMoveCommand.of(scheduleId, requestTripperId, targetDayId, targetOrder);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getTargetDayId()).isEqualTo(targetDayId);
        assertThat(command.getTargetOrder()).isEqualTo(targetOrder);
    }

    @DisplayName("tatgetOrder가 null -> 검증 에러")
    @Test
    public void nullTargetOrderTest() {
        // given
        long scheduleId = 1L;
        long requestTripperId = 2L;
        Long targetDayId = 1L;
        Integer targetOrder = null;

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> ScheduleMoveCommand.of(scheduleId, requestTripperId, targetDayId, targetOrder),
                CustomValidationException.class);


        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
    }

    @DisplayName("tatgetOrder가 음수 -> 검증 에러")
    @Test
    public void negativeTargetOrderTest() {
        // given
        long scheduleId = 1L;
        long requestTripperId = 2L;
        Long targetDayId = 1L;
        Integer targetOrder = -1;


        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> ScheduleMoveCommand.of(scheduleId, requestTripperId, targetDayId, targetOrder),
                CustomValidationException.class);


        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
    }

}
