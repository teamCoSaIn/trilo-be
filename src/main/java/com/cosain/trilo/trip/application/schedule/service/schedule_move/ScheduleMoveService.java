package com.cosain.trilo.trip.application.schedule.service.schedule_move;

import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleMoveAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TooManyDayScheduleException;
import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.exception.MidScheduleIndexConflictException;
import com.cosain.trilo.trip.domain.exception.ScheduleIndexRangeException;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ScheduleMoveService {

    private final ScheduleRepository scheduleRepository;
    private final DayRepository dayRepository;

    @Transactional
    public ScheduleMoveResult moveSchedule(ScheduleMoveCommand command) {
        Schedule schedule = findSchedule(command.getScheduleId());
        Day targetDay = findTargetDay(command.getTargetDayId());

        Trip trip = schedule.getTrip();

        validateScheduleMoveAuthority(trip, command.getRequestTripperId());
        validateTargetDayScheduleCount(schedule, targetDay);

        ScheduleMoveDto moveDto;
        try {
            moveDto = trip.moveSchedule(schedule, targetDay, command.getTargetOrder());
        } catch (MidScheduleIndexConflictException | ScheduleIndexRangeException e) {
            // 일정 이동 과정에서 순서 충돌이 발생하거나, 범위를 벗어나면 재배치후 다시 가져옴
            scheduleRepository.relocateDaySchedules(trip.getId(), command.getTargetDayId());
            schedule = findSchedule(command.getScheduleId());
            targetDay = findTargetDay(command.getTargetDayId());

            trip = schedule.getTrip();
            moveDto = trip.moveSchedule(schedule, targetDay, command.getTargetOrder()); // 다시 이동
        }
        return ScheduleMoveResult.from(moveDto);
    }

    private void validateTargetDayScheduleCount(Schedule schedule, Day targetDay) {
        Long beforeDayId = schedule.getDay() == null ? null : schedule.getDay().getId();
        Long afterDayId = targetDay == null ? null : targetDay.getId();

        if (Objects.equals(beforeDayId, afterDayId) || afterDayId == null) {
            return;
        }
        if (scheduleRepository.findDayScheduleCount(afterDayId) == Day.MAX_DAY_SCHEDULE_COUNT) {
            throw new TooManyDayScheduleException("옮기려는 Day 자리에 일정이 가득참");
        }
    }


    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findByIdWithTrip(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일치하는 식별자의 일정을 찾을 수 없음"));
    }

    private Day findTargetDay(Long targetDayId) {
        if (targetDayId == null) {
            return null;
        }
        return dayRepository.findByIdWithTrip(targetDayId)
                .orElseThrow(() -> new DayNotFoundException("일치하는 식별자의 Day를 찾을 수 없음"));
    }

    private void validateScheduleMoveAuthority(Trip trip, Long requestTripperId) {
        if (!trip.getTripperId().equals(requestTripperId)) {
            throw new NoScheduleMoveAuthorityException("권한 없는 사람이 일정을 이동하려 함");
        }
    }
}
