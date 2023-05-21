package com.cosain.trilo.trip.query.infra.repository.schedule;

import com.cosain.trilo.trip.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.query.infra.repository.schedule.jpa.ScheduleQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final ScheduleQueryJpaRepository scheduleQueryJpaRepository;

    @Override
    public Optional<ScheduleDto> findScheduleDetailByScheduleId(Long id) {
        Optional<ScheduleDetail> scheduleDetail = scheduleQueryJpaRepository.findScheduleDetailById(id);

        return scheduleDetail.isEmpty() ? Optional.empty() : Optional.of(ScheduleDto.from(scheduleDetail.get()));
    }

    @Override
    public Slice<ScheduleDto> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable) {
        Slice<ScheduleDetail> scheduleDetails = scheduleQueryJpaRepository.findTemporaryScheduleListByTripId(tripId, pageable);
        List<ScheduleDto> scheduleDtos = scheduleDetails.map(ScheduleDto::from).getContent();
        return new SliceImpl<>(scheduleDtos, pageable, scheduleDetails.hasNext());
    }
}

