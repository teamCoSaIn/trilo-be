package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.common.exception.schedule.InvalidScheduleMoveTargetOrderException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 일정 이동에 필요한 명령(command, 비즈니스 입력 모델)입니다.
 */
@Getter
@EqualsAndHashCode(of = {"scheduleId", "requestTripperId", "targetDayId", "targetOrder"})
public class ScheduleMoveCommand {

    /**
     * 옮기고자 하는 일정의 식별자(id)
     */
    private final long scheduleId;

    /**
     * 일정 이동을 시도하는 여행자(사용자)의 식별자(id)
     */
    private final long requestTripperId;

    /**
     * 도착지의 Day 식별자(id)
     */
    private final Long targetDayId;

    /**
     * 도착지의 몇 번째 순서로 이동할 것인지
     */
    private final int targetOrder;

    /**
     * 일정 이동 명령(비즈니스 입력 모델)을 생성합니다.
     *
     * @param scheduleId       옮기고자 하는 일정의 식별자(id)
     * @param requestTripperId 일정 이동을 시도하는 여행자(사용자)의 식별자(id)
     * @param targetDayId      도착지의 Day 식별자(id)
     * @param targetOrder      도착지의 몇 번째 순서로 이동할 것인지
     * @return 일정 이동 명령
     * @throws CustomValidationException 명령 생성과정에서 발생한 예외들을 묶은 예외
     */
    public static ScheduleMoveCommand of(long scheduleId, long requestTripperId, Long targetDayId, Integer targetOrder)
            throws CustomValidationException {

        List<CustomException> exceptions = new ArrayList<>(); // 발생 예외를 수집할 예외 수집기
        validateTargetOrder(targetOrder, exceptions); // targetOrder 검증

        if (!exceptions.isEmpty()) {
            // 입력 검증 과정에서 예외가 하나라도 발생할 경우 이들을 모아서, 검증 예외를 발생시킴.
            throw new CustomValidationException(exceptions);
        }
        return new ScheduleMoveCommand(scheduleId, requestTripperId, targetDayId, targetOrder);
    }

    /**
     * <p>일정이 옮겨질 순서가 올바른 지 검증합니다.</p>
     * <p>검증 과정에서 문제가 확인되면 예외 수집기에 예외를 수집합니다.</p>
     * @param targetOrder 요청한 순서
     * @param exceptions 검증 과정에서 발생한 예외를 수집할 컬렉션
     */
    private static void validateTargetOrder(Integer targetOrder, List<CustomException> exceptions) {
        if (targetOrder == null || targetOrder < 0) {
            exceptions.add(new InvalidScheduleMoveTargetOrderException("순서값이 null 또는 음수"));
        }
    }

    private ScheduleMoveCommand(long scheduleId, long requestTripperId, Long targetDayId, int targetOrder) {
        this.scheduleId = scheduleId;
        this.requestTripperId = requestTripperId;
        this.targetDayId = targetDayId;
        this.targetOrder = targetOrder;
    }
}
