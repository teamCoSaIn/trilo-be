package com.cosain.trilo.trip.presentation.trip.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TripSearchRequest {
    private String query;
    @NotNull(message = "정렬 기준은 필수값 입니다. ex) RECENT, LIKE")
    private SortType sortType;
    @NotNull(message = "사이즈는 필수값 입니다.")
    private Integer size;
    private Long tripId;

    private TripSearchRequest(){}

    public TripSearchRequest(String query, SortType sortType, Integer size, Long tripId){
        this.query = query;
        this.sortType = sortType;
        this.size = size;
        this.tripId = tripId;
    }

    public enum SortType{
        LIKE,
        RECENT
    }
}
