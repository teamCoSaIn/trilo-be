package com.cosain.trilo.trip.presentation.trip.query.dto.response;

import com.cosain.trilo.trip.application.trip.query.usecase.dto.TripPageResult;
import com.cosain.trilo.trip.application.trip.query.usecase.dto.TripResult;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TripPageResponse {

    private boolean hasNext;
    private List<TripDetailResponse> trips;

    public static TripPageResponse from(TripPageResult tripPageResult){
        List<TripResult> tripResults = tripPageResult.getTrips();
        List<TripDetailResponse> tripDetails = tripResults.stream()
                .map(TripDetailResponse::from)
                .collect(Collectors.toList());
        return new TripPageResponse(tripPageResult.isHasNext(), tripDetails);
    }

    private TripPageResponse(boolean hasNext, List<TripDetailResponse> trips){
        this.hasNext = hasNext;
        this.trips = trips;
    }


}
