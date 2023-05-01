package com.cosain.trilo.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Modifying
    @Query("DELETE FROM Schedule as s where s.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);

    @Query("SELECT s" +
            " FROM Schedule as s JOIN FETCH s.trip" +
            " WHERE s.id = :id")
    Optional<Schedule> findByIdWithTrip(@Param("id") Long scheduleId);
}
