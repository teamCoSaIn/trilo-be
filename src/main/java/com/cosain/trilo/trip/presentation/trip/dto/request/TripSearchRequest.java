package com.cosain.trilo.trip.presentation.trip.dto.request;

import jakarta.validation.constraints.Max;
import lombok.Getter;

@Getter
public class TripSearchRequest {

    private final int DEFAULT_SIZE = 8;

    private String query;
    private SortType sortType;
    @Max(value = 100, message = "size는 최대 100 이하여야 합니다.")
    private Integer size;
    private Long tripId;

    private TripSearchRequest(){}

    public TripSearchRequest(String query, String sortType, Integer size, Long tripId){
        this.query = query;
        this.sortType = SortType.of(sortType);
        this.size = size == null ? DEFAULT_SIZE : size;
        this.tripId = tripId;
    }

    public enum SortType{
        LIKE, RECENT;
        public static SortType of(String sortTypeStr){

            for(SortType st : SortType.values()){
                if(st.equals(sortTypeStr)){
                    return st;
                }
            }

            return SortType.RECENT;
        }
    }
}
