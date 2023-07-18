package com.cosain.trilo.trip.application.trip.service.trip_detail_search;

import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripDetailSearchService {

    private final TripQueryDAO tripQueryDAO;

    public TripDetail searchTripDetail(Long tripId) {

        TripDetail tripDetail = findTripDetail(tripId);
        return tripDetail;
    }

    private TripDetail findTripDetail(Long tripId){
        return tripQueryDAO.findTripDetailById(tripId).orElseThrow(TripNotFoundException::new);
    }

}
