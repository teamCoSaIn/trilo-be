package com.cosain.trilo.trip.application.trip.service.temporary_search;

import com.cosain.trilo.trip.application.dao.ScheduleQueryDAO;
import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporarySearchService {

    private final TripQueryDAO tripQueryDAO;
    private final ScheduleQueryDAO scheduleQueryDAO;

    public TempScheduleListSearchResult searchTemporary(TempScheduleListQueryParam queryParam) {
        verifyTripExists(queryParam.getTripId());
        verifyScheduleExists(queryParam.getScheduleId());
        return scheduleQueryDAO.findTemporarySchedules(queryParam);
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

}
