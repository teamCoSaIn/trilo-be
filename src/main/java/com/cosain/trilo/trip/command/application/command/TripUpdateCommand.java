package com.cosain.trilo.trip.command.application.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripUpdateCommand {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder(access = AccessLevel.PUBLIC)
    private TripUpdateCommand(String title, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
