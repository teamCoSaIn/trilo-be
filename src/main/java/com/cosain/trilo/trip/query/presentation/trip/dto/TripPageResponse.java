package com.cosain.trilo.trip.query.presentation.trip.dto;

import com.cosain.trilo.trip.query.application.dto.TripPageResult;
import com.cosain.trilo.trip.query.application.dto.TripResult;
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
