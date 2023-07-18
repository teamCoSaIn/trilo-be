package com.cosain.trilo.trip.application.trip.service.trip_list_search;

import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPageCondition;
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
public class TripListSearchService {

    private final TripQueryDAO tripQueryDAO;
    private final UserRepository userRepository;
    private final TripImageOutputAdapter tripImageOutputAdapter;

    public Slice<TripSummary> searchTripSummaries(TripPageCondition tripPageCondition, Pageable pageable) {
        verifyTripperExists(tripPageCondition.getTripperId());
        Slice<TripSummary> tripSummaries = findTripSummaries(tripPageCondition, pageable);
        tripSummaries.forEach(this::updateImageURL);
        return tripSummaries;
    }

    private void verifyTripperExists(Long tripperId) {
        userRepository.findById(tripperId)
                .orElseThrow(() -> new TripperNotFoundException("해당 식별자의 사용자(여행자)를 찾지 못 함"));
    }

    private Slice<TripSummary> findTripSummaries(TripPageCondition tripPageCondition, Pageable pageable) {
        return tripQueryDAO.findTripSummariesByTripperId(tripPageCondition, pageable);
    }

    private void updateImageURL(TripSummary tripSummary) {
        String imageName = tripSummary.getImageURL(); // 실제 DB에 저장된 이미지 이름
        String fullImageURL = tripImageOutputAdapter.getFullTripImageURL(imageName);
        tripSummary.updateImageURL(fullImageURL);
    }
}
