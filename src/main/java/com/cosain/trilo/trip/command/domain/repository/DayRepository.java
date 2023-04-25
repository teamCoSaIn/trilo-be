package com.cosain.trilo.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DayRepository extends JpaRepository<Day, Long> {

    @Modifying
    @Query("delete from Day d where d in :days")
    void deleteDays(@Param("days") List<Day> days);
}
