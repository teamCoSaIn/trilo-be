package com.cosain.trilo.trip.query.presentation.trip.dto;

import com.cosain.trilo.trip.query.domain.dto.TripDto;
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
    public static TripDetailResponse from(TripDto tripDto){
        return TripDetailResponse.builder()
                .tripId(tripDto.getId())
                .title(tripDto.getTitle())
                .status(tripDto.getStatus())
                .startDate(tripDto.getStartDate())
                .endDate(tripDto.getEndDate())
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
