package com.cosain.trilo.trip.query.infra.repository.day;

import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.query.infra.repository.day.jpa.DayQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DayScheduleQueryRepositoryImpl implements DayScheduleQueryRepository {

    private final DayQueryJpaRepository dayQueryJpaRepository;

    @Override
    public Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId) {
        return dayQueryJpaRepository.findDayWithSchedulesByDayId(dayId);
    }
}
