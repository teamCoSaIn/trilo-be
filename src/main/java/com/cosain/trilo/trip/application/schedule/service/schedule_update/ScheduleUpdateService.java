package com.cosain.trilo.trip.application.schedule.service.schedule_update;

import com.cosain.trilo.common.exception.schedule.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleUpdateAuthorityException;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleUpdateService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void updateSchedule(ScheduleUpdateCommand command) {
        Schedule schedule = findSchedule(command.getScheduleId());
        validateScheduleUpdateAuthority(schedule, command.getRequestTripperId());

        schedule.changeTitle(command.getScheduleTitle());
        schedule.changeContent(command.getScheduleContent());
        schedule.changeTime(command.getScheduleTime());
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findByIdWithTrip(scheduleId).orElseThrow(() -> new ScheduleNotFoundException("일정이 존재하지 않습니다."));
    }

    private void validateScheduleUpdateAuthority(Schedule schedule, Long tripperId) {
        if (!schedule.getTrip().getTripperId().equals(tripperId)) {
            throw new NoScheduleUpdateAuthorityException("일정을 수정할 권한이 없습니다");
        }
    }

}
