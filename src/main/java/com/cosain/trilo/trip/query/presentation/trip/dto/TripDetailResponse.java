package com.cosain.trilo.trip.query.presentation.trip.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripDetailResponse {
    private final Long tripId;
    private final String title;
    private final String status;
    private LocalDate startDate;
    private LocalDate endDate;
    public static TripDetailResponse of(Long tripId, String title, String status, LocalDate startDate, LocalDate endDate){
        return TripDetailResponse.builder()
                .tripId(tripId)
                .title(title)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private TripDetailResponse(final Long tripId, final String title,final String status, LocalDate startDate, LocalDate endDate) {
        this.tripId = tripId;
        this.title = title;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
