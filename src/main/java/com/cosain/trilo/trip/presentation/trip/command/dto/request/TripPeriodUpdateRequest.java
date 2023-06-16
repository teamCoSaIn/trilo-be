package com.cosain.trilo.trip.presentation.trip.command.dto.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPeriodUpdateRequest {

    private LocalDate startDate;
    private LocalDate endDate;

    public TripPeriodUpdateRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
