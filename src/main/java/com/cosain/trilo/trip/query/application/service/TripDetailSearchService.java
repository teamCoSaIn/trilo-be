package com.cosain.trilo.trip.query.application.service;

import com.cosain.trilo.trip.query.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.query.application.exception.NoTripDetailSearchAuthorityException;
import com.cosain.trilo.trip.query.application.usecase.TripDetailSearchUseCase;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripDetailSearchService implements TripDetailSearchUseCase {

    private final TripQueryRepository tripQueryRepository;

    @Override
    public TripDetailResponse searchTripDetail(Long tripId, Long tripperId) {

        TripDetail tripDetail = findTripDetail(tripId);
        validateTripDetailQueryAuthority(tripDetail, tripperId);
        return TripDetailResponse.of(tripDetail.getId(), tripDetail.getTitle(), tripDetail.getStatus(), tripDetail.getStartDate(), tripDetail.getEndDate());
    }

    private TripDetail findTripDetail(Long tripId){
        return tripQueryRepository.findTripDetailByTripId(tripId).orElseThrow(TripNotFoundException::new);
    }

    private void validateTripDetailQueryAuthority(TripDetail tripDetail, Long tripperId){
        if (tripDetail.getTripperId() != tripperId) {
            throw new NoTripDetailSearchAuthorityException("여행 단건 조회 권한이 없습니다.");
        }
    }

}
