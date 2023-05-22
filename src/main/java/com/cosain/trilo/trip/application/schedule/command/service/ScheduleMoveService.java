package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.exception.NoScheduleMoveAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.command.usecase.ScheduleMoveUseCase;
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

@RequiredArgsConstructor
@Service
public class ScheduleMoveService implements ScheduleMoveUseCase {

    private final ScheduleRepository scheduleRepository;
    private final DayRepository dayRepository;

    @Override
    @Transactional
    public ScheduleMoveResult moveSchedule(Long scheduleId, Long moveTripperId, ScheduleMoveCommand moveCommand) {
        Schedule schedule = findSchedule(scheduleId);
        Day targetDay = findDay(moveCommand.getTargetDayId());

        Trip trip = schedule.getTrip();

        validateScheduleMoveAuthority(trip, moveTripperId);

        ScheduleMoveDto moveDto;
        try {
            moveDto = trip.moveSchedule(schedule, targetDay, moveCommand.getTargetOrder());
        } catch (MidScheduleIndexConflictException | ScheduleIndexRangeException e) {
            scheduleRepository.relocateDaySchedules(trip.getId(), moveCommand.getTargetDayId());
            schedule = findSchedule(scheduleId);
            targetDay = findDay(moveCommand.getTargetDayId());

            trip = schedule.getTrip();
            moveDto = trip.moveSchedule(schedule, targetDay, moveCommand.getTargetOrder());
        }
        return ScheduleMoveResult.from(moveDto);
    }


    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findByIdWithTrip(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일치하는 식별자의 일정을 찾을 수 없음"));
    }

    private Day findDay(Long targetDayId) {
        if (targetDayId == null) {
            return null;
        }
        return dayRepository.findByIdWithTrip(targetDayId)
                .orElseThrow(() -> new DayNotFoundException("일치하는 식별자의 Day를 찾을 수 없음"));
    }

    private void validateScheduleMoveAuthority(Trip trip, Long moveTripperId) {
        if (!trip.getTripperId().equals(moveTripperId)) {
            throw new NoScheduleMoveAuthorityException("권한 없는 사람이 일정을 이동하려 함");
        }
    }
}
