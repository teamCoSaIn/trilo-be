package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.NoTripDetailSearchAuthorityException;
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

    public TripDetail searchTripDetail(Long tripId, Long tripperId) {

        TripDetail tripDetail = findTripDetail(tripId);
        validateTripDetailQueryAuthority(tripDetail, tripperId);
        return tripDetail;
    }

    private TripDetail findTripDetail(Long tripId){
        return tripQueryRepository.findTripDetailById(tripId).orElseThrow(TripNotFoundException::new);
    }

    private void validateTripDetailQueryAuthority(TripDetail tripDetail, Long tripperId){
        if (tripDetail.getTripperId() != tripperId) {
            throw new NoTripDetailSearchAuthorityException("여행 단건 조회 권한이 없습니다.");
        }
    }

}
