package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t" +
            " FROM Trip as t LEFT JOIN FETCH t.days" +
            " WHERE t.id = :id")
    Optional<Trip> findByIdWithDays(@Param("id") Long tripId);
}
