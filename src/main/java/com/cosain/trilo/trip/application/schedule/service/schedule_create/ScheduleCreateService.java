package com.cosain.trilo.trip.application.schedule.service.schedule_create;

import com.cosain.trilo.common.exception.day.DayNotFoundException;
import com.cosain.trilo.common.exception.day.InvalidTripDayException;
import com.cosain.trilo.common.exception.schedule.NoScheduleCreateAuthorityException;
import com.cosain.trilo.common.exception.schedule.ScheduleIndexRangeException;
import com.cosain.trilo.common.exception.schedule.TooManyDayScheduleException;
import com.cosain.trilo.common.exception.schedule.TooManyTripScheduleException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleUpdateAuthorityException;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 일정 생성을 수행하는 애플리케이션 서비스입니다.
 */
@RequiredArgsConstructor
@Service
public class ScheduleCreateService {

    /**
     * 일정을 저장, 관리하고 있는 리포지토리
     */
    private final ScheduleRepository scheduleRepository;

    /**
     * Day를 저장, 관리하고 있는 리포지토리
     */
    private final DayRepository dayRepository;

    /**
     * 여행을 저장, 관리하고 있는 리포지토리
     */
    private final TripRepository tripRepository;

    /**
     * 일정을 생성합니다.
     * @param command 일정 생성 명령
     * @return 생성된 일정의 식별자(id)
     * @throws TripNotFoundException 일치하는 식별자의 여행이 존재하지 않을 때
     * @throws DayNotFoundException 일치하는 식별자의 Day가 존재하지 않을 때
     * @throws NoScheduleUpdateAuthorityException 일정을 생성할 권한이 없을 때
     * @throws TooManyTripScheduleException 여행이 가질 수 있는 일정 갯수 제한을 초과할 때
     * @throws TooManyDayScheduleException Day가 가진 일정의 갯수 제한을 넘을 때
     * @throws InvalidTripDayException     Day가 Trip의 여행이 아닐 때
     */
    @Transactional
    public Long createSchedule(ScheduleCreateCommand command)
            throws TripNotFoundException, DayNotFoundException, NoScheduleCreateAuthorityException,
            TooManyTripScheduleException, TooManyDayScheduleException, InvalidTripDayException{

        // Trip 조회 -> 없으면 예외 발생
        Trip trip = findTrip(command.getTripId());

        // 여행을 일정과 조회 -> 없으면 예외 발생
        Day targetDay = findTargetDayWithTrip(command.getTargetDayId());

        // 일정을 생성할 권한이 있는 지 검증 -> 없으면 예외 발생
        validateCreateAuthority(trip, command.getRequestTripperId());

        // 여행, Day의 일정 최대 보유 갯수 제약을 넘는 지 검증 -> 생성할 수 없으면 예외 발생
        validateTripScheduleCount(command.getTripId());
        validateDayScheduleCount(command.getTargetDayId());

        Schedule schedule = createSchedule(trip, targetDay, command);
        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    /**
     * 여행을 조회해옵니다.
     * @param tripId 여행의 식별자
     * @return 여행
     * @throws TripNotFoundException 일치하는 식별자의 여행이 존재하지 않을 때
     */
    private Trip findTrip(Long tripId) throws TripNotFoundException {
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("trip이 존재하지 않음"));
    }

    /**
     * Day와 여행을 함께 조회해옵니다.
     * @param dayId Day의 식별자
     * @return dayId가 null이면 null, 일치하는 식별자의 Day가 있으면 Day
     * @throws DayNotFoundException 일치하는 식별자의 Day가 존재하지 않을 때
     */
    private Day findTargetDayWithTrip(Long dayId) throws DayNotFoundException {
        return (dayId == null)
                ? null
                : dayRepository.findByIdWithTrip(dayId).orElseThrow(() -> new DayNotFoundException("Schedule을 Day에 넣으려고 했는데, 해당하는 Day가 존재하지 않음."));
    }

    /**
     * 요청 사용자가 일정을 생성할 권한이 있는 지 검증합니다.
     * @param trip 여행
     * @param requestTripperId 요청 사용자(여행자)의 id
     * @throws NoScheduleUpdateAuthorityException 일정을 생성할 권한이 없을 때
     */
    private void validateCreateAuthority(Trip trip, Long requestTripperId) throws NoScheduleCreateAuthorityException {
        if (!trip.getTripperId().equals(requestTripperId)) {
            throw new NoScheduleCreateAuthorityException("여행의 소유주가 아닌 사람이, 일정을 생성하려고 함");
        }
    }

    /**
     * 일정 생성 시, 여행이 가질 수 있는 일정 갯수 제한에 위배되지 않는 지 검증합니다.
     * @param tripId 여행 식별자(id)
     * @throws TooManyTripScheduleException 여행이 가질 수 있는 일정 갯수 제한을 초과할 때
     */
    private void validateTripScheduleCount(Long tripId) throws TooManyTripScheduleException {
        // 여행이 가진 일정의 갯수 조회
        int tripScheduleCount = scheduleRepository.findTripScheduleCount(tripId);

        if (tripScheduleCount == Trip.MAX_TRIP_SCHEDULE_COUNT) {
            throw new TooManyTripScheduleException("일정 생성 시도 -> 여행 최대 일정 갯수 초과");
        }
    }

    /**
     * 일정을 Day로 옮길 때, 허용된 최대 일정 갯수 범위를 만족하는 지 검증합니다.
     * @param dayId day의 식별자
     * @throws TooManyDayScheduleException Day가 가진 일정의 갯수 제한을 넘을 때
     */
    private void validateDayScheduleCount(Long dayId) {
        if (dayId == null) {
            return;
        }
        int dayScheduleCount = scheduleRepository.findDayScheduleCount(dayId);

        if (dayScheduleCount == Day.MAX_DAY_SCHEDULE_COUNT) {
            throw new TooManyDayScheduleException("일정 생성 시도 -> Day의 최대 일정 갯수 초과");
        }
    }

    /**
     * <p>일정 생성을 실제로 수행합니다.</p>
     * <p>이 메서드를 호출한 이후 여행, Day 변수는 다시 사용할 수 없으니 주의하십시오</p>
     * @param trip 일정이 소속될 여행
     * @param targetDay 일정이 소속될 Day(null 일 경우 임시 보관함)
     * @param command 일정 생성 명령
     * @return 생성된 일정
     * @throws InvalidTripDayException     Day가 Trip의 여행이 아닐 때
     */
    private Schedule createSchedule(Trip trip, Day targetDay, ScheduleCreateCommand command) throws InvalidTripDayException {
        try {
            return trip.createSchedule(targetDay, command.getScheduleTitle(), command.getPlace());
        } catch (ScheduleIndexRangeException e) {
            // 일정 생성 과정에서 범위 예외가 발생할 경우, 해당 Day/임시보관함의 일정을 전체 재배치
            scheduleRepository.relocateDaySchedules(command.getTripId(), command.getTargetDayId());

            // 다시 생성
            return retryCreateSchedule(command);
        }
    }

    /**
     * Day 또는 임시보관함의 일정이 재배치 된 상황에서, 일정을 다시 생성합니다.
     * @param command 일정 생성 명령
     * @return 생성된 일정
     */
    private Schedule retryCreateSchedule(ScheduleCreateCommand command) {
        // 재조회
        Trip trip = findTrip(command.getTripId());
        Day targetDay = findTargetDayWithTrip(command.getTargetDayId());

        // 일정 생성
        return trip.createSchedule(targetDay, command.getScheduleTitle(), command.getPlace());
    }

}
