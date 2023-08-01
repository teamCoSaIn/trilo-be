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

    /**
     * 인자로 전달받은 식별자의 일정(속한 여행 포함)을 담은 Optional을 조회하여 반환받습니다.
     * @param scheduleId 일정의 id(식별자)
     * @return 조회해온 일정(속한 여행 포함)을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    @Override
    public Optional<Schedule> findByIdWithTrip(Long scheduleId) {
        return jpaScheduleRepository.findByIdWithTrip(scheduleId);
    }

    /**
     * Day또는 임시보관함의 일정들을 일괄 재배치합니다.
     * @param tripId 소속된 Trip의 Id
     * @param dayId 소속된 Day의 Id(null일 경우 임시보관함으로 간주)
     * @return 변경 영향을 받은 일정의 갯수
     */
    @Override
    public int relocateDaySchedules(Long tripId, Long dayId) {
        return jpaScheduleRepository.relocateDaySchedules(tripId, dayId);
    }

    /**
     * 전달받은 Day들에 속한 일정들을 여행의 임시보관함 맨 뒤로 옮깁니다.
     * @param tripId 여행의 id
     * @param dayIds 일정들이 속한 Day들의 Id
     * @return 이동된 일정의 갯수
     */
    @Override
    public int moveSchedulesToTemporaryStorage(Long tripId, List<Long> dayIds) {
        return jpaScheduleRepository.moveSchedulesToTemporaryStorage(tripId, dayIds);
    }

    @Override
    public int findTripScheduleCount(Long tripId) {
        return jpaScheduleRepository.findTripScheduleCount(tripId);
    }

    /**
     * 전달받은 식별자의 Day가 가진 일정의 갯수를 반환받습니다.
     * @param dayId Day의 id
     * @return Day가 가진 일정의 갯수
     */
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
