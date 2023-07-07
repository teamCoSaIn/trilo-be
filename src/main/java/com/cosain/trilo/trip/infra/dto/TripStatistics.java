package com.cosain.trilo.trip.infra.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TripStatistics {

    private final Long totalTripCnt;
    private final Long terminatedTripCnt;

    @QueryProjection
    public TripStatistics(Long totalTripCnt, Long terminatedTripCnt) {
        this.totalTripCnt = totalTripCnt;
        this.terminatedTripCnt = terminatedTripCnt;
    }
}
