package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripSummary {

    private long id;
    private long tripperId;
    private String title;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    @QueryProjection
    public TripSummary(long id, long tripperId, String title, Enum status, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status.name();
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
