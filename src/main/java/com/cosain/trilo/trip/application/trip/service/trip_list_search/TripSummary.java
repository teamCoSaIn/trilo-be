package com.cosain.trilo.trip.application.trip.service.trip_list_search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripSummary {

    private long tripId;
    private long tripperId;
    private String title;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
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
