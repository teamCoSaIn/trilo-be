package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.schedule.query.usecase.dto.ScheduleResult;
import com.cosain.trilo.trip.application.trip.query.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemporarySearchService implements TemporarySearchUseCase {

    private final TripQueryRepository tripQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;

    @Override
    public Slice<ScheduleDetail> searchTemporary(Long tripId, Pageable pageable) {

        verifyTripExists(tripId);
        Slice<ScheduleDetail> scheduleDetails = findTemporaryScheduleListByTripId(tripId, pageable);
        return scheduleDetails;
    }

    private List<ScheduleResult> mapToScheduleResults(Slice<ScheduleDto> scheduleDtos) {
        return scheduleDtos.getContent()
                .stream()
                .map(ScheduleResult::from)
                .collect(Collectors.toList());
    }

    private void verifyTripExists(Long tripId) {
        if(!tripQueryRepository.existById(tripId)){
            throw new TripNotFoundException();
        }
    }

    private Slice<ScheduleDetail> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable){
        return scheduleQueryRepository.findTemporaryScheduleListByTripId(tripId, pageable);
    }


}
