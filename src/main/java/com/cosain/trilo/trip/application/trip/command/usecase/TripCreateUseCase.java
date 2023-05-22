package com.cosain.trilo.trip.application.trip.command.usecase;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripCreateCommand;

public interface TripCreateUseCase {

    Long createTrip(Long tripperId, TripCreateCommand createDto);
}
