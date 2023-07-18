package com.cosain.trilo.trip.application.trip.service;

import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripDetailSearchService {

    private final TripQueryRepository tripQueryRepository;

    public TripDetail searchTripDetail(Long tripId) {

        TripDetail tripDetail = findTripDetail(tripId);
        return tripDetail;
    }

    private TripDetail findTripDetail(Long tripId){
        return tripQueryRepository.findTripDetailById(tripId).orElseThrow(TripNotFoundException::new);
    }

}
