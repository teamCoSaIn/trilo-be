package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.NoTripDetailSearchAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.query.service.dto.TripResult;
import com.cosain.trilo.trip.domain.dto.TripDto;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
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
    public TripResult searchTripDetail(Long tripId, Long tripperId) {

        TripDto tripDto = findTripDetail(tripId);
        validateTripDetailQueryAuthority(tripDto, tripperId);
        return TripResult.from(tripDto);
    }

    private TripDto findTripDetail(Long tripId){
        return tripQueryRepository.findTripDetailByTripId(tripId).orElseThrow(TripNotFoundException::new);
    }

    private void validateTripDetailQueryAuthority(TripDto tripDto, Long tripperId){
        if (tripDto.getTripperId() != tripperId) {
            throw new NoTripDetailSearchAuthorityException("여행 단건 조회 권한이 없습니다.");
        }
    }

}
