package com.cosain.trilo.trip.query.application.service;

import com.cosain.trilo.trip.query.application.dto.ScheduleResult;
import com.cosain.trilo.trip.query.application.dto.TemporaryPageResult;
import com.cosain.trilo.trip.query.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.query.application.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.query.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
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
    public TemporaryPageResult searchTemporary(Long tripId, Pageable pageable) {

        verifyTripExists(tripId);
        Slice<ScheduleDto> scheduleDtos = findTemporaryScheduleListByTripId(tripId, pageable);
        List<ScheduleResult> scheduleResults = mapToScheduleResults(scheduleDtos);
        return TemporaryPageResult.of(scheduleResults, scheduleDtos.hasNext());
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

    private Slice<ScheduleDto> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable){
        return scheduleQueryRepository.findTemporaryScheduleListByTripId(tripId, pageable);
    }


}
