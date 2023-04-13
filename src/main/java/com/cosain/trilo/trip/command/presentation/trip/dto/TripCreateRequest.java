package com.cosain.trilo.trip.command.presentation.trip.dto;

import com.cosain.trilo.trip.command.application.command.TripCreateCommand;
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
    public TripCreateCommand toCommand() {
        return new TripCreateCommand(title);
    }
}
