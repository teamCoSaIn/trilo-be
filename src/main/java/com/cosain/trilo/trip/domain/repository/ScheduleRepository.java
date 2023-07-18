package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Schedule;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    Schedule save(Schedule schedule);

    Optional<Schedule> findById(Long scheduleId);

    Optional<Schedule> findByIdWithTrip(@Param("scheduleId") Long scheduleId);

    int relocateDaySchedules(@Param("tripId") Long tripId, @Param("dayId") Long dayId);

    int moveSchedulesToTemporaryStorage(@Param("tripId") Long tripId, @Param("dayIds") List<Long> dayIds);

    int findTripScheduleCount(@Param("tripId") Long tripId);

    int findDayScheduleCount(@Param("dayId") Long dayId);

    void delete(Schedule schedule);

    void deleteAllByTripId(@Param("tripId") Long tripId);

    void deleteAllByTripIds(@Param("tripIdsForDelete") List<Long> tripIdsForDelete);
}
