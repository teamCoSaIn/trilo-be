package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripDetail {

    private final long id;

    private final long tripperId;

    private final String title;

    private final String status;

    private LocalDate startDate;

    private LocalDate endDate;

    @QueryProjection
    public TripDetail(final long id,final long tripperId,final String title, Enum status, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status.name();
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
