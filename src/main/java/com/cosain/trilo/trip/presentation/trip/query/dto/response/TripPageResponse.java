package com.cosain.trilo.trip.presentation.trip.query.dto.response;

import com.cosain.trilo.trip.infra.dto.TripSummary;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class TripPageResponse {

    private boolean hasNext;
    private List<TripSummary> trips;

    public static TripPageResponse from(Slice<TripSummary> tripSummaries){
        return new TripPageResponse(tripSummaries.hasNext(), tripSummaries.getContent());
    }

    private TripPageResponse(boolean hasNext, List<TripSummary> trips){
        this.hasNext = hasNext;
        this.trips = trips;
    }


}
