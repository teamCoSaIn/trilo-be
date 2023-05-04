package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;
import com.cosain.trilo.trip.command.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.command.application.exception.NoScheduleCreateAuthorityException;
import com.cosain.trilo.trip.command.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.command.application.usecase.ScheduleCreateUseCase;
import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
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
        /**
         * TODO : 임시보관함 고려해서 수정하기
         */
        Day day = findDay(createCommand.getDayId());
        Trip trip = findTrip(createCommand.getTripId());
        validateCreateAuthority(trip, tripperId);

        Schedule schedule = makeSchedule(trip, day, createCommand);
        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    private Day findDay(Long dayId){
        return (dayId == null)
                ? null
                : dayRepository.findById(dayId).orElseThrow(() -> new DayNotFoundException("Schedule을 Day에 넣으려고 했는데, 해당하는 Day가 존재하지 않음."));
    }

    private Trip findTrip(Long tripId){
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("trip이 존재하지 않음"));
    }

    private void validateCreateAuthority(Trip trip, Long tripperId) {
        if(!trip.getTripperId().equals(tripperId)){
            throw new NoScheduleCreateAuthorityException("여행의 소유주가 아닌 사람이, 일정을 생성하려고 함");
        }
    }

    private Schedule makeSchedule(Trip trip, Day day, ScheduleCreateCommand createCommand) {
        return trip.createSchedule(day, createCommand.getTitle(), createCommand.getPlace());
    }

}
