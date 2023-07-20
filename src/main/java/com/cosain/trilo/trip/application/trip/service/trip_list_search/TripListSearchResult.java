package com.cosain.trilo.trip.application.trip.service.trip_list_search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TripListSearchResult {

    private final boolean hasNext;
    private final List<TripSummary> trips;

    public static TripListSearchResult of(boolean hasNext, List<TripSummary> tripSummaries) {
        return new TripListSearchResult(hasNext, tripSummaries);
    }

    public static TripListSearchResult from(Slice<TripSummary> tripSummaries){
        return new TripListSearchResult(tripSummaries.hasNext(), tripSummaries.getContent());
    }

    private TripListSearchResult(boolean hasNext, List<TripSummary> trips){
        this.hasNext = hasNext;
        this.trips = trips;
    }

    @Getter
    public static class TripSummary {

        private final long tripId;
        private final long tripperId;
        private final String title;
        private final String status;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private String imageURL;

        @QueryProjection
        public TripSummary(long tripId, long tripperId, String title, Enum status, LocalDate startDate, LocalDate endDate, String imageName) {
            this.tripId = tripId;
            this.tripperId = tripperId;
            this.title = title;
            this.status = status.name();
            this.startDate = startDate;
            this.endDate = endDate;
            this.imageURL = imageName;
        }

        public void updateImageURL(String imageURL){
            this.imageURL = imageURL;
        }
    }
}
