package com.cosain.trilo.trip.command.application.command;

import lombok.Getter;

@Getter
public class TripCreateCommand {

    private String title;
    public TripCreateCommand(String title) {
        this.title = title;
    }
}
