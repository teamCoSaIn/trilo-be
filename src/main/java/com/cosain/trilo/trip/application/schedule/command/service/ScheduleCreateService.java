package com.cosain.trilo.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.exception.*;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleCreateCommand;
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
public class ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;
    private final DayRepository dayRepository;
    private final TripRepository tripRepository;

    @Transactional
    public Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand) {
        Long dayId = createCommand.getDayId();
        Long tripId = createCommand.getTripId();

        Day day = findDay(dayId);
        Trip trip = findTrip(tripId);
        validateCreateAuthority(trip, tripperId);
        validateTripScheduleCount(tripId);
        validateDayScheduleCount(dayId);

        Schedule schedule;
        try {
            schedule = trip.createSchedule(day, createCommand.getScheduleTitle(), createCommand.getPlace());
        } catch (ScheduleIndexRangeException e) {
            // 기존 ScheduleIndex 뒤에 일정 생성을 시도했으나, 가능한 ScheduleIndex 범위를 벗어났으므로 전체 재정렬
            scheduleRepository.relocateDaySchedules(tripId, dayId);

            // 영속성 컨텍스트가 초기화 됐으므로 trip, day를 다시 가져오고 다시 작업
            trip = findTrip(trip.getId());
            day = findDay(dayId);
            schedule =  trip.createSchedule(day, createCommand.getScheduleTitle(), createCommand.getPlace());
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

    private void validateTripScheduleCount(Long tripId) {
        int tripScheduleCount = scheduleRepository.findTripScheduleCount(tripId);

        if (tripScheduleCount == Trip.MAX_TRIP_SCHEDULE_COUNT) {
            throw new TooManyTripScheduleException("여행 생성 시도 -> 여행 최대 일정 갯수 초과");
        }
    }

    private void validateDayScheduleCount(Long dayId) {
        if (dayId == null) {
            return;
        }
        int dayScheduleCount = scheduleRepository.findDayScheduleCount(dayId);

        if (dayScheduleCount == Day.MAX_DAY_SCHEDULE_COUNT) {
            throw new TooManyDayScheduleException("여행 생성 시도 -> Day의 최대 일정 갯수 초과");
        }
    }

}
