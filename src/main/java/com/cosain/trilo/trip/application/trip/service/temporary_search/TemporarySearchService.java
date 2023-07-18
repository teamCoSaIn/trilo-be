package com.cosain.trilo.trip.application.trip.service.temporary_search;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.dao.ScheduleQueryDAO;
import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporarySearchService {

    private final TripQueryDAO tripQueryDAO;
    private final ScheduleQueryDAO scheduleQueryDAO;

    public Slice<ScheduleSummary> searchTemporary(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable) {

        verifyTripExists(tripId);
        verifyScheduleExists(tempSchedulePageCondition.getScheduleId());
        Slice<ScheduleSummary> scheduleSummaries = findTemporaryScheduleListByTripId(tripId, tempSchedulePageCondition,pageable);
        return scheduleSummaries;
    }

    private void verifyScheduleExists(Long scheduleId) {
        if(scheduleId != null && !scheduleQueryDAO.existById(scheduleId))
            throw new ScheduleNotFoundException();
    }

    private void verifyTripExists(Long tripId) {
        if(!tripQueryDAO.existById(tripId)){
            throw new TripNotFoundException();
        }
    }


    private Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable){
        return scheduleQueryDAO.findTemporaryScheduleListByTripId(tripId, tempSchedulePageCondition, pageable);
    }


}
