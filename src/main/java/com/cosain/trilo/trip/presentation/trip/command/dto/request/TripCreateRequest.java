package com.cosain.trilo.trip.presentation.trip.command.dto.request;

import com.cosain.trilo.trip.application.trip.command.service.dto.TripCreateCommand;
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
