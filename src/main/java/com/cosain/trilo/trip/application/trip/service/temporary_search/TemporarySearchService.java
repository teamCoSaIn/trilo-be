package com.cosain.trilo.trip.application.trip.service.temporary_search;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporarySearchService {

    private final TripQueryRepository tripQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;

    public Slice<ScheduleSummary> searchTemporary(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable) {

        verifyTripExists(tripId);
        verifyScheduleExists(tempSchedulePageCondition.getScheduleId());
        Slice<ScheduleSummary> scheduleSummaries = findTemporaryScheduleListByTripId(tripId, tempSchedulePageCondition,pageable);
        return scheduleSummaries;
    }

    private void verifyScheduleExists(Long scheduleId) {
        if(scheduleId != null && !scheduleQueryRepository.existById(scheduleId))
            throw new ScheduleNotFoundException();
    }

    private void verifyTripExists(Long tripId) {
        if(!tripQueryRepository.existById(tripId)){
            throw new TripNotFoundException();
        }
    }


    private Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable){
        return scheduleQueryRepository.findTemporaryScheduleListByTripId(tripId, tempSchedulePageCondition, pageable);
    }


}
