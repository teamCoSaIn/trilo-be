package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Day;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Day 엔티티 또는 Day 엔티티에 관한 정보를 조회해오거나 등록/수정/삭제하는 리포지토리입니다.
 */
public interface DayRepository {

    void saveAll(List<Day> days);

    Optional<Day> findByIdWithTrip(@Param("dayId") Long dayId);

    int deleteAllByIds(@Param("dayIds") List<Long> dayIds);

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 Day들을 모두 제거합니다.
     * @param tripId 여행의 식별자(id)
     */
    void deleteAllByTripId(@Param("tripId") Long tripId);

    void deleteAllByTripIds(@Param("tripIds") List<Long> tripIds);
}
