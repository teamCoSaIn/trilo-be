package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.service.dto.TripCreateCommand;

public interface TripCreateUseCase {

    Long createTrip(Long tripperId, TripCreateCommand createDto);
}
