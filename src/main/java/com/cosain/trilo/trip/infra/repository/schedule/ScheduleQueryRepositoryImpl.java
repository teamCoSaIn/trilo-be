package com.cosain.trilo.trip.infra.repository.schedule;

import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.infra.repository.schedule.jpa.ScheduleQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final ScheduleQueryJpaRepository scheduleQueryJpaRepository;

    @Override
    public Optional<ScheduleDetail> findScheduleDetailByScheduleId(Long id) {
        return scheduleQueryJpaRepository.findScheduleDetailById(id);
    }

    @Override
    public Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable) {
        return scheduleQueryJpaRepository.findTemporaryScheduleListByTripId(tripId, pageable);
    }
}

