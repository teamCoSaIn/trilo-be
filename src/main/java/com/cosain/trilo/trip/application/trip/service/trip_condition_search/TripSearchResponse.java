package com.cosain.trilo.trip.application.trip.service.trip_condition_search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TripSearchResponse {

    private final boolean hasNext;
    private final List<TripSummary> trips;

    public TripSearchResponse(boolean hasNext, List<TripSummary> trips) {
        this.hasNext = hasNext;
        this.trips = trips;
    }

    public static TripSearchResponse of(boolean hasNext, List<TripSummary> trips){
        return new TripSearchResponse(hasNext, trips);
    }

    @Getter
    public static class TripSummary{
        private final Long tripId;
        private final Long tripperId;
        private final int period;
        private final String title;
        private String imageURL;

        @QueryProjection
        public TripSummary(Long tripId, Long tripperId, LocalDate startDate, LocalDate endDate, String title, String imageURL) {
            this.tripId = tripId;
            this.tripperId = tripperId;
            this.period = endDate.compareTo(startDate);
            this.title = title;
            this.imageURL = imageURL;
        }

        public void updateImageURL(String imageURL){
            this.imageURL = imageURL;
        }
    }
}
