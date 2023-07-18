package com.cosain.trilo.unit.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_move.ScheduleMoveCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleMoveTargetOrderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("ScheduleMoveCommandFactory 테스트")
public class ScheduleMoveCommandFactoryTest {

    private ScheduleMoveCommandFactory scheduleMoveCommandFactory = new ScheduleMoveCommandFactory();

    @DisplayName("tatgetOrder가 null -> 검증 에러")
    @Test
    public void nullTargetOrderTest() {
        // given
        Long targetDayId = 1L;
        Integer targetOrder = null;


        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleMoveCommandFactory.createCommand(targetDayId, targetOrder),
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
        Long targetDayId = 1L;
        Integer targetOrder = -1;


        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleMoveCommandFactory.createCommand(targetDayId, targetOrder),
                CustomValidationException.class);


        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleMoveTargetOrderException.class);
    }

    @DisplayName("targetOrder가 음이 아닌 정수 -> 성공")
    @Test
    public void successTest() {
        // given
        Long targetDayId = 1L;
        Integer targetOrder = 0;


        // when
        ScheduleMoveCommand command = scheduleMoveCommandFactory.createCommand(targetDayId, targetOrder);


        // then
        assertThat(command).isNotNull();
        assertThat(command.getTargetDayId()).isEqualTo(targetDayId);
        assertThat(command.getTargetOrder()).isEqualTo(targetOrder);
    }
}
