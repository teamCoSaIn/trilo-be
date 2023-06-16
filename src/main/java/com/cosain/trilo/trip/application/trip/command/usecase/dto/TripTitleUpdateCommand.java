package com.cosain.trilo.trip.application.trip.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.Getter;

@Getter
public class TripTitleUpdateCommand {

    private TripTitle tripTitle;

    public TripTitleUpdateCommand(TripTitle tripTitle) {
        this.tripTitle = tripTitle;
    }
}
