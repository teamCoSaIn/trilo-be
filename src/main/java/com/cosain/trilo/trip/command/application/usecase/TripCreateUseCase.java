package com.cosain.trilo.trip.command.application.usecase;

import com.cosain.trilo.trip.command.application.command.TripCreateCommand;

public interface TripCreateUseCase {

    Long createTrip(Long tripperId, TripCreateCommand createDto);
}
