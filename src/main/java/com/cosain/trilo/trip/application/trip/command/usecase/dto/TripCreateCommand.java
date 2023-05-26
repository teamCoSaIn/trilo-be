package com.cosain.trilo.trip.application.trip.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.Getter;

@Getter
public class TripCreateCommand {

    private TripTitle tripTitle;

    public static TripCreateCommand from(String rawTitle) {
        return new TripCreateCommand(TripTitle.of(rawTitle));
    }

    private TripCreateCommand(TripTitle tripTitle) {
        this.tripTitle = tripTitle;
    }
}
