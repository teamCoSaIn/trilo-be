package com.cosain.trilo.trip.presentation.trip.command.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripUpdateRequest {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    public TripUpdateRequest(String title, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
