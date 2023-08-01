package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.common.exception.day.DayNotFoundException;
import com.cosain.trilo.common.exception.day.InvalidTripDayException;
import com.cosain.trilo.common.exception.schedule.*;
import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 일정 이동을 수행하는 애플리케이션 서비스입니다.
 */
@RequiredArgsConstructor
@Service
public class ScheduleMoveService {

    /**
     * 일정을 저장, 관리하고 있는 리포지토리
     */
    private final ScheduleRepository scheduleRepository;

    /**
     * Day를 저장, 관리하고 있는 리포지토리
     */
    private final DayRepository dayRepository;

    /**
     * 일정을 이동시킵니다.
     * @param command 일정 이동 명령(비즈니스 입력 모델)
     * @return 일정 이동 결과
     * @throws ScheduleNotFoundException 일정을 찾을 수 없을 때
     * @throws DayNotFoundException 도착지 Day를 찾을 수 없을 때
     * @throws NoScheduleMoveAuthorityException 일정을 이동할 권한이 없을 때
     * @throws TooManyDayScheduleException 도착지 Day가 가진 일정의 갯수 제한을 넘을 때
     * @throws InvalidTripDayException 도착지 Day가 일정이 속한 Trip의 Day가 아닐 때
     * @throws InvalidScheduleMoveTargetOrderException 요청한 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     */
    @Transactional
    public ScheduleMoveResult moveSchedule(ScheduleMoveCommand command)
            throws ScheduleNotFoundException, DayNotFoundException, NoScheduleMoveAuthorityException, TooManyDayScheduleException,
            InvalidTripDayException, InvalidScheduleMoveTargetOrderException {

        // 일정을 여행과 조회 -> 없으면 예외 발생
        Schedule schedule = findScheduleWithTrip(command.getScheduleId());

        // Day를 여행과 조회 -> 없으면 예외 발생
        Day targetDay = findTargetDayWithTrip(command.getTargetDayId());
        Trip trip = schedule.getTrip();

        // 일정을 이동시킬 권한이 있는 지 검증 -> 권한 없으면 예외 발생
        validateScheduleMoveAuthority(trip, command.getRequestTripperId());

        // 옮겨질 Day의 일정 최대 보유 갯수 제약을 넘는 지 검증 -> 이동할 수 없으면 예외 발생
        validateTargetDayScheduleCount(schedule, targetDay);

        // 일정 이동
        // 주의!!! 영속성 컨텍스트 초기화 가능성 때문에 이 코드보다 아래에서 schedule, trip, day 변수를 그대로 사용할 수 없음
        return moveSchedule(schedule, trip, targetDay, command);
    }

    /**
     * 일정을 일정이 속한 Trip과 함께 조회해옵니다.
     *
     * @param scheduleId 이동하고자 하는 일정의 식별자(id)
     * @return 일정
     * @throws ScheduleNotFoundException 일정이 존재하지 않을 경우
     */
    private Schedule findScheduleWithTrip(Long scheduleId) throws ScheduleNotFoundException {
        return scheduleRepository.findByIdWithTrip(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일치하는 식별자의 일정을 찾을 수 없음"));
    }

    /**
     * 일정의 도착지 Day를 Trip과 함께 조회해옵니다.
     *
     * @param targetDayId 도착지 Day의 식별자
     * @return Day
     * @throws DayNotFoundException Day가 존재하지 않을 경우
     */
    private Day findTargetDayWithTrip(Long targetDayId) throws DayNotFoundException {
        if (targetDayId == null) {
            return null;
        }
        return dayRepository.findByIdWithTrip(targetDayId)
                .orElseThrow(() -> new DayNotFoundException("일치하는 식별자의 Day를 찾을 수 없음"));
    }

    /**
     * 요청 사용자(여행자)가 일정을 이동할 권한이 있는 지 검증합니다.
     * @param trip 여행
     * @param requestTripperId 요청 사용자(여행자)의 id
     * @throws NoScheduleMoveAuthorityException 일정을 이동할 권한이 없을 때
     */
    private void validateScheduleMoveAuthority(Trip trip, Long requestTripperId) throws NoScheduleMoveAuthorityException {
        if (!trip.getTripperId().equals(requestTripperId)) {
            throw new NoScheduleMoveAuthorityException("권한 없는 사람이 일정을 이동하려 함");
        }
    }

    /**
     * 일정을 Day로 옮길 때, 허용된 최대 일정 갯수 범위를 만족하는 지 검증합니다.
     * @param schedule 일정
     * @param targetDay 도착지 Day
     * @throws TooManyDayScheduleException 도착지 Day가 가진 일정의 갯수 제한을 넘을 때
     */
    private void validateTargetDayScheduleCount(Schedule schedule, Day targetDay) throws TooManyDayScheduleException {
        Long beforeDayId = schedule.getDay() == null ? null : schedule.getDay().getId();
        Long afterDayId = targetDay == null ? null : targetDay.getId();

        // 도착지가 출발지 Day와 같거나, 도착지가 임시보관함일 경우 검증 필요 없음
        if (Objects.equals(beforeDayId, afterDayId) || afterDayId == null) {
            return;
        }

        // 일정을 Day로 옮길 때, Day의 최대 일정 갯수 제한을 초과하는 경우 예외 발생
        if (scheduleRepository.findDayScheduleCount(afterDayId) == Day.MAX_DAY_SCHEDULE_COUNT) {
            throw new TooManyDayScheduleException("옮기려는 Day 자리에 일정이 가득참");
        }
    }

    /**
     * <p>일정 이동을 실제로 수행합니다.</p>
     * <p>이 메서드를 호출한 이후 일정, 여행, 대상 Day는 다시 사용할 수 없으니 주의하십시오.</p>
     * @param schedule  옮길 일정
     * @param trip      일정이 속한 여행
     * @param targetDay 대상 Day(null일 경우 임시보관함)
     * @param command   일정 이동 명령
     * @return 일정 이동 결과
     * @throws InvalidTripDayException 도착 Day가 일정이 속한 Trip의 Day가 아닐 때
     * @throws InvalidScheduleMoveTargetOrderException 요청한 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     */
    private ScheduleMoveResult moveSchedule(Schedule schedule, Trip trip, Day targetDay, ScheduleMoveCommand command)
            throws InvalidTripDayException, InvalidScheduleMoveTargetOrderException {

        ScheduleMoveDto moveDto;
        try {
            moveDto = trip.moveSchedule(schedule, targetDay, command.getTargetOrder());
        } catch (MidScheduleIndexConflictException | ScheduleIndexRangeException e) {
            // 이동 과정에서 순서 충돌이 발생하거나, 범위를 벗어나면 재배치 후 다시 이동시켜야함
            // 주의!!! 영속성 컨텍스트 초기화 때문에 아래에서는 위의 schedule, trip, day 변수를 그대로 사용할 수 없음
            scheduleRepository.relocateDaySchedules(trip.getId(), command.getTargetDayId());

            // 다시 이동
            moveDto = retryMoveSchedule(command);
        }
        return ScheduleMoveResult.from(moveDto);
    }

    /**
     * 일정이 재배치 된 상황에서, 일정을 다시 이동시킵니다.
     * @param command 일정 이동 명령
     * @return 일정 이동 결과
     */
    private ScheduleMoveDto retryMoveSchedule(ScheduleMoveCommand command) {
        // 재조회
        Schedule schedule = findScheduleWithTrip(command.getScheduleId());
        Day targetDay = findTargetDayWithTrip(command.getTargetDayId());
        Trip trip = schedule.getTrip();

        // 이동
        return trip.moveSchedule(schedule, targetDay, command.getTargetOrder());
    }
}
