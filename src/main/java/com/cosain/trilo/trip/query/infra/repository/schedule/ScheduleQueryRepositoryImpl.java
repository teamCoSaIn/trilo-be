package com.cosain.trilo.trip.query.infra.repository.schedule;

import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.query.infra.repository.schedule.jpa.ScheduleQueryJpaRepository;
import lombok.RequiredArgsConstructor;
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
}
