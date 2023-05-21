package com.cosain.trilo.trip.application.trip.query.service.dto;

import com.cosain.trilo.trip.query.domain.dto.TripDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripResult {
    private long id;
    private long tripperId;
    private String title;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder(access = AccessLevel.PRIVATE)
    private TripResult(long id, long tripperId, String title, String status, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TripResult from(TripDto tripDto){
        return TripResult.builder()
                .id(tripDto.getId())
                .tripperId(tripDto.getTripperId())
                .title(tripDto.getTitle())
                .status(tripDto.getStatus())
                .startDate(tripDto.getStartDate())
                .endDate(tripDto.getEndDate())
                .build();
    }
}
