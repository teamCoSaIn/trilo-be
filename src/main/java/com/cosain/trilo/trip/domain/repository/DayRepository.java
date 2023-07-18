package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Day;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DayRepository {

    void saveAll(List<Day> days);

    Optional<Day> findByIdWithTrip(@Param("dayId") Long dayId);

    int deleteAllByIds(@Param("dayIds") List<Long> dayIds);

    void deleteAllByTripId(@Param("tripId") Long tripId);

    void deleteAllByTripIds(@Param("tripIds") List<Long> tripIds);
}
