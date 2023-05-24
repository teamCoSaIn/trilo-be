package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.application.trip.query.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TripListSearchService implements TripListSearchUseCase {

    private final TripQueryRepository tripQueryRepository;
    private final UserRepository userRepository;

    @Override
    public Slice<TripDetail> searchTripDetails(Long tripperId, Pageable pageable) {

        verifyTripperExists(tripperId);
        Slice<TripDetail> tripDetails = findTripDtos(tripperId, pageable);
        return tripDetails;
    }

    private void verifyTripperExists(Long tripperId){
        userRepository.findById(tripperId).orElseThrow(TripperNotFoundException::new);
    }

    private Slice<TripDetail> findTripDtos(Long tripperId, Pageable pageable){
        return tripQueryRepository.findTripDetailListByTripperId(tripperId, pageable);
    }

}
