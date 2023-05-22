package com.cosain.trilo.trip.application.trip.command.usecase.dto;

import lombok.Getter;

@Getter
public class TripCreateCommand {

    private String title;
    public TripCreateCommand(String title) {
        this.title = title;
    }
}
