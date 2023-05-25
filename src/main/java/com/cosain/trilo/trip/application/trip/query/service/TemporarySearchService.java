package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.query.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporarySearchService implements TemporarySearchUseCase {

    private final TripQueryRepository tripQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;

    @Override
    public Slice<ScheduleSummary> searchTemporary(Long tripId, Pageable pageable) {

        verifyTripExists(tripId);
        Slice<ScheduleSummary> scheduleSummaries = findTemporaryScheduleListByTripId(tripId, pageable);
        return scheduleSummaries;
    }

    private void verifyTripExists(Long tripId) {
        if(!tripQueryRepository.existById(tripId)){
            throw new TripNotFoundException();
        }
    }

    private Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable){
        return scheduleQueryRepository.findTemporaryScheduleListByTripId(tripId, pageable);
    }


}
