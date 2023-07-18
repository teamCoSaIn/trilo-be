package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Trip;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository {

    Trip save(Trip trip);

    Optional<Trip> findById(Long tripId);

    Optional<Trip> findByIdWithDays(@Param("id") Long tripId);

    List<Trip> findAllByTripperId(@Param("tripperId") Long tripperId);

    void delete(Trip trip);

    void deleteAllByTripperId(@Param("tripperId") Long tripperId);
}
