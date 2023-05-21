package com.cosain.trilo.trip.domain.dto;

import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripDto {
    private long id;
    private long tripperId;
    private String title;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder(access = AccessLevel.PRIVATE)
    private TripDto(long id, long tripperId, String title, String status, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TripDto from(TripDetail tripDetail){
        return TripDto.builder()
                .id(tripDetail.getId())
                .tripperId(tripDetail.getTripperId())
                .title(tripDetail.getTitle())
                .status(tripDetail.getStatus())
                .startDate(tripDetail.getStartDate())
                .endDate(tripDetail.getEndDate())
                .build();
    }
}
