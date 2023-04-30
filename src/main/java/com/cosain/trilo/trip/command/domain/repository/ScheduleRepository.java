package com.cosain.trilo.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Modifying
    @Query("DELETE FROM Schedule as s where s.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);

    @Query("SELECT s FROM Schedule as s JOIN FETCH Trip WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithTrip(@Param("scheduleId") Long scheduleId);
}
