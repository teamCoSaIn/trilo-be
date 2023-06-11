package com.cosain.trilo.trip.infra.repository.day;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.repository.day.jpa.DayQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DayQueryRepositoryImpl implements DayQueryRepository {

    private final DayQueryJpaRepository dayQueryJpaRepository;

    @Override
    public Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId) {
        return dayQueryJpaRepository.findDayWithSchedulesByDayId(dayId);
    }

    @Override
    public List<DayScheduleDetail> findDayScheduleListByTripId(Long tripId){
        return dayQueryJpaRepository.findDayScheduleListByTripId(tripId);
    }
}
