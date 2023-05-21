package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.schedule.command.service.dto.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleCreateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.exception.ScheduleIndexRangeException;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScheduleCreateService implements ScheduleCreateUseCase {

    private final ScheduleRepository scheduleRepository;
    private final DayRepository dayRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand) {
        Long dayId = createCommand.getDayId();
        Long tripId = createCommand.getTripId();

        Day day = findDay(dayId);
        Trip trip = findTrip(tripId);
        validateCreateAuthority(trip, tripperId);

        Schedule schedule;
        try {
            schedule = trip.createSchedule(day, createCommand.getTitle(), createCommand.getPlace());
        } catch (ScheduleIndexRangeException e) {
            scheduleRepository.relocateDaySchedules(tripId, dayId);
            trip = findTrip(trip.getId());
            day = findDay(dayId);
            schedule =  trip.createSchedule(day, createCommand.getTitle(), createCommand.getPlace());
        }
        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    private Day findDay(Long dayId){
        return (dayId == null)
                ? null
                : dayRepository.findByIdWithTrip(dayId).orElseThrow(() -> new DayNotFoundException("Schedule을 Day에 넣으려고 했는데, 해당하는 Day가 존재하지 않음."));
    }

    private Trip findTrip(Long tripId){
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("trip이 존재하지 않음"));
    }

    private void validateCreateAuthority(Trip trip, Long tripperId) {
        if(!trip.getTripperId().equals(tripperId)){
            throw new NoScheduleCreateAuthorityException("여행의 소유주가 아닌 사람이, 일정을 생성하려고 함");
        }
    }

}
