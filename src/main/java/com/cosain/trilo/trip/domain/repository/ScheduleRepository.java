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

    Optional<Schedule> findByIdWithTrip(Long scheduleId);

    /**
     * Day또는 임시보관함의 일정들을 일괄 재배치합니다.
     * @param tripId 소속된 Trip의 Id
     * @param dayId 소속된 Day의 Id(null일 경우 임시보관함으로 간주)
     * @return 변경 영향을 받은 일정의 갯수
     */
    int relocateDaySchedules(@Param("tripId") Long tripId, @Param("dayId") Long dayId);

    /**
     * 전달받은 Day들에 속한 일정들을 여행의 임시보관함 맨 뒤로 옮깁니다.
     * @param tripId 여행의 id
     * @param dayIds 일정들이 속한 Day들의 Id
     * @return 이동된 일정의 갯수
     */
    int moveSchedulesToTemporaryStorage(Long tripId, List<Long> dayIds);

    int findTripScheduleCount(Long tripId);

    int findDayScheduleCount(Long dayId);

    void delete(Schedule schedule);

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 일정들을 모두 제거합니다.
     * @param tripId 여행의 식별자(id)
     */
    void deleteAllByTripId(Long tripId);

    void deleteAllByTripIds(List<Long> tripIdsForDelete);
}
