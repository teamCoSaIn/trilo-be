package com.cosain.trilo.trip.presentation.trip.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripCreateRequest {

    private String title;

    public TripCreateRequest(String title) {
        this.title = title;
    }

}
