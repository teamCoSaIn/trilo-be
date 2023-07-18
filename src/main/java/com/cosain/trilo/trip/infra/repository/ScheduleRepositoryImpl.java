package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final JpaScheduleRepository jpaScheduleRepository;

    @Override
    public Schedule save(Schedule schedule) {
        return jpaScheduleRepository.save(schedule);
    }

    @Override
    public Optional<Schedule> findById(Long scheduleId) {
        return jpaScheduleRepository.findById(scheduleId);
    }

    @Override
    public Optional<Schedule> findByIdWithTrip(Long scheduleId) {
        return jpaScheduleRepository.findByIdWithTrip(scheduleId);
    }

    @Override
    public int relocateDaySchedules(Long tripId, Long dayId) {
        return jpaScheduleRepository.relocateDaySchedules(tripId, dayId);
    }

    @Override
    public int moveSchedulesToTemporaryStorage(Long tripId, List<Long> dayIds) {
        return jpaScheduleRepository.moveSchedulesToTemporaryStorage(tripId, dayIds);
    }

    @Override
    public int findTripScheduleCount(Long tripId) {
        return jpaScheduleRepository.findTripScheduleCount(tripId);
    }

    @Override
    public int findDayScheduleCount(Long dayId) {
        return jpaScheduleRepository.findDayScheduleCount(dayId);
    }

    @Override
    public void delete(Schedule schedule) {
        jpaScheduleRepository.delete(schedule);
    }

    @Override
    public void deleteAllByTripId(@Param("tripId") Long tripId) {
        jpaScheduleRepository.deleteAllByTripId(tripId);
    }

    @Override
    public void deleteAllByTripIds(List<Long> tripIdsForDelete) {
        jpaScheduleRepository.deleteAllByTripIds(tripIdsForDelete);
    }
}
