package com.cosain.trilo.trip.presentation.day.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayColorUpdateRequest {

    private String colorName;

    public DayColorUpdateRequest(String colorName) {
        this.colorName = colorName;
    }
}
