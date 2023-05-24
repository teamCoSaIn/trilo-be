package com.cosain.trilo.trip.presentation.trip.query.dto.response;

import com.cosain.trilo.trip.infra.dto.TripDetail;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TripPageResponse {

    private boolean hasNext;
    private List<TripDetailResponse> trips;

    public static TripPageResponse from(Slice<TripDetail> tripDetails){
        List<TripDetailResponse> tripDetailResponseList = tripDetails.stream()
                .map(TripDetailResponse::from)
                .collect(Collectors.toList());
        return new TripPageResponse(tripDetails.hasNext(), tripDetailResponseList);
    }

    private TripPageResponse(boolean hasNext, List<TripDetailResponse> trips){
        this.hasNext = hasNext;
        this.trips = trips;
    }


}
