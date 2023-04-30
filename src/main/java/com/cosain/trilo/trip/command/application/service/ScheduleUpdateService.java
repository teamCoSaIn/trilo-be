package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.ScheduleUpdateCommand;
import com.cosain.trilo.trip.command.application.exception.NoScheduleUpdateAuthorityException;
import com.cosain.trilo.trip.command.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.command.application.usecase.ScheduleUpdateUseCase;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleUpdateService implements ScheduleUpdateUseCase {

    private final ScheduleRepository scheduleRepository;
    @Override
    public Long updateSchedule(Long scheduleId, Long tripperId, ScheduleUpdateCommand scheduleUpdateCommand) {

        Schedule schedule = findSchedule(scheduleId);
        validateScheduleUpdateAuthority(schedule, tripperId);

        schedule.changeTitle(scheduleUpdateCommand.getTitle());
        schedule.changeContent(scheduleUpdateCommand.getContent());

        scheduleRepository.save(schedule);

        return schedule.getId();
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleNotFoundException("일정이 존재하지 않습니다."));
    }

    private void validateScheduleUpdateAuthority(Schedule schedule, Long tripperId){
        if(!schedule.getTrip().getTripperId().equals(tripperId)){
            throw new NoScheduleUpdateAuthorityException("일정을 수정할 권한이 없습니다");
        }
    }

}
