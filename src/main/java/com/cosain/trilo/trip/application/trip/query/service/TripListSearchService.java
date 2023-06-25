package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.application.trip.query.usecase.TripListSearchUseCase;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
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
    private final TripImageOutputAdapter tripImageOutputAdapter;

    @Override
    public Slice<TripSummary> searchTripSummaries(TripPageCondition tripPageCondition, Pageable pageable) {

        verifyTripperExists(tripPageCondition.getTripperId());
        Slice<TripSummary> tripSummaries = findTripSummaries(tripPageCondition, pageable);
        updateImagePath(tripSummaries);
        return tripSummaries;
    }

    private void updateImagePath(Slice<TripSummary> tripSummaries) {
        tripSummaries.stream().forEach(tripSummary ->
                tripSummary.updateImageUrl(tripImageOutputAdapter.getTripImageFullPath(tripSummary.getImagePath())));
    }

    private void verifyTripperExists(Long tripperId){
        userRepository.findById(tripperId).orElseThrow(TripperNotFoundException::new);
    }

    private Slice<TripSummary> findTripSummaries(TripPageCondition tripPageCondition, Pageable pageable){
        return tripQueryRepository.findTripSummariesByTripperId(tripPageCondition, pageable);
    }

}
