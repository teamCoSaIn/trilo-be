package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.exception.NoScheduleDeleteAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScheduleDeleteService implements ScheduleDeleteUseCase {

    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId, Long deleteTripperId) {
        Schedule schedule = findSchedule(scheduleId);
        validateDeleteAuthority(schedule, deleteTripperId);
        scheduleRepository.delete(schedule);
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findByIdWithTrip(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일치하는 식별자의 일정을 찾을 수 없음"));
    }

    private void validateDeleteAuthority(Schedule schedule, Long deleteTripperId) {
        Long tripperId = schedule.getTrip().getTripperId();

        if (!tripperId.equals(deleteTripperId)) {
            throw new NoScheduleDeleteAuthorityException("여행의 소유주가 아닌 사람이 일정을 삭제하려고 시도함");
        }
    }
}
