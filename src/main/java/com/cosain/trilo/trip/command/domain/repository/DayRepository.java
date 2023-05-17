package com.cosain.trilo.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DayRepository extends JpaRepository<Day, Long> {

    @Query("SELECT d " +
            "FROM Day as d JOIN FETCH d.trip " +
            "WHERE d.id = :dayId")
    Optional<Day> findByIdWithTrip(@Param("dayId") Long dayId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Day d WHERE d in :dayIds")
    int deleteAllByIds(@Param("dayIds") List<Long> dayIds);

    @Modifying
    @Query("DELETE FROM Day as d WHERE d.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);
}
