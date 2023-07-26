package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Schedule;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 일정 엔티티 또는 일정 엔티티에 관한 정보를 조회해오거나 등록/수정/삭제하는 리포지토리입니다.
 */
public interface ScheduleRepository {

    Schedule save(Schedule schedule);

    Optional<Schedule> findById(Long scheduleId);

    Optional<Schedule> findByIdWithTrip(@Param("scheduleId") Long scheduleId);

    int relocateDaySchedules(@Param("tripId") Long tripId, @Param("dayId") Long dayId);

    int moveSchedulesToTemporaryStorage(@Param("tripId") Long tripId, @Param("dayIds") List<Long> dayIds);

    int findTripScheduleCount(@Param("tripId") Long tripId);

    int findDayScheduleCount(@Param("dayId") Long dayId);

    void delete(Schedule schedule);

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 일정들을 모두 제거합니다.
     * @param tripId 여행의 식별자(id)
     */
    void deleteAllByTripId(@Param("tripId") Long tripId);

    void deleteAllByTripIds(@Param("tripIdsForDelete") List<Long> tripIdsForDelete);
}
