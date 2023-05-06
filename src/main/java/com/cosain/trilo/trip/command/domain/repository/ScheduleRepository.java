package com.cosain.trilo.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s" +
            " FROM Schedule as s JOIN FETCH s.trip" +
            " WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithTrip(@Param("scheduleId") Long scheduleId);

    @Modifying
    @Query("DELETE FROM Schedule as s where s.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Schedule s " +
            "SET s.scheduleIndex.value = (" +
            "  SELECT COUNT(s2) " +
            "  FROM Schedule s2 " +
            "  WHERE (:dayId is not null AND s2.day.id = :dayId AND s2.scheduleIndex.value < s.scheduleIndex.value) " +
            "        OR (:dayId is null AND s2.day is null AND s2.trip.id = :tripId AND s2.scheduleIndex.value < s.scheduleIndex.value) " +
            ") * 10000000 " +
            "WHERE s.trip.id = :tripId " +
            "  AND ((:dayId is not null AND s.day.id = :dayId) OR (:dayId is null AND s.day is null))")
    int relocateDaySchedules(@Param("tripId") Long tripId, @Param("dayId") Long dayId);
}
