package com.cosain.trilo.trip.application.trip.service.trip_condition_search;

import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripConditionSearchService {

    private final TripQueryDAO tripQueryDAO;
    private final TripImageOutputAdapter tripImageOutputAdapter;

    @Transactional(readOnly = true)
    public TripSearchResponse findBySearchConditions(TripSearchRequest request){
        TripSearchResponse searchResponse = tripQueryDAO.findWithSearchConditions(request);
        searchResponse.getTrips().forEach(this::updateImageURL);
        return searchResponse;
    }

    private void updateImageURL(TripSearchResponse.TripSummary tripSummary) {
        String imageName = tripSummary.getImageURL(); // 실제 DB에 저장된 이미지 이름
        String fullImageURL = tripImageOutputAdapter.getFullTripImageURL(imageName);
        tripSummary.updateImageURL(fullImageURL);
    }
}
