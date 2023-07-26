package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 일정 엔티티 또는 일정 엔티티에 관한 정보를 조회해오거나 등록/수정/삭제하는 리포지토리 구현체입니다.
 * @see ScheduleRepository
 */
@Component
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    /**
     * Schedule 엔티티를 관리하는 Spring Data JPA Repository
     */
    private final JpaScheduleRepository jpaScheduleRepository;

    @Override
    public Schedule save(Schedule schedule) {
        return jpaScheduleRepository.save(schedule);
    }

    @Override
    public Optional<Schedule> findById(Long scheduleId) {
        return jpaScheduleRepository.findById(scheduleId);
    }

    @Override
    public Optional<Schedule> findByIdWithTrip(Long scheduleId) {
        return jpaScheduleRepository.findByIdWithTrip(scheduleId);
    }

    @Override
    public int relocateDaySchedules(Long tripId, Long dayId) {
        return jpaScheduleRepository.relocateDaySchedules(tripId, dayId);
    }

    @Override
    public int moveSchedulesToTemporaryStorage(Long tripId, List<Long> dayIds) {
        return jpaScheduleRepository.moveSchedulesToTemporaryStorage(tripId, dayIds);
    }

    @Override
    public int findTripScheduleCount(Long tripId) {
        return jpaScheduleRepository.findTripScheduleCount(tripId);
    }

    @Override
    public int findDayScheduleCount(Long dayId) {
        return jpaScheduleRepository.findDayScheduleCount(dayId);
    }

    @Override
    public void delete(Schedule schedule) {
        jpaScheduleRepository.delete(schedule);
    }

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 일정들을 모두 제거합니다.
     * @param tripId 여행의 식별자(id)
     */
    @Override
    public void deleteAllByTripId(@Param("tripId") Long tripId) {
        jpaScheduleRepository.deleteAllByTripId(tripId);
    }

    @Override
    public void deleteAllByTripIds(List<Long> tripIdsForDelete) {
        jpaScheduleRepository.deleteAllByTripIds(tripIdsForDelete);
    }
}
