package com.cosain.trilo.trip.application.trip.service.trip_list_search;

import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripListSearchService {

    private final TripQueryDAO tripQueryDAO;
    private final UserRepository userRepository;
    private final TripImageOutputAdapter tripImageOutputAdapter;

    @Transactional(readOnly = true)
    public TripListSearchResult searchTripList(TripListQueryParam queryParam) {
        verifyTripperExists(queryParam.getTripperId());

        var tripListSearchResult = tripQueryDAO.findTripSummariesByTripperId(queryParam);
        tripListSearchResult.getTrips().forEach(this::updateImageURL);
        return tripListSearchResult;
    }

    private void verifyTripperExists(Long tripperId) {
        userRepository.findById(tripperId)
                .orElseThrow(() -> new TripperNotFoundException("해당 식별자의 사용자(여행자)를 찾지 못 함"));
    }

    private void updateImageURL(TripListSearchResult.TripSummary tripSummary) {
        String imageName = tripSummary.getImageURL(); // 실제 DB에 저장된 이미지 이름
        String fullImageURL = tripImageOutputAdapter.getFullTripImageURL(imageName);
        tripSummary.updateImageURL(fullImageURL);
    }
}
