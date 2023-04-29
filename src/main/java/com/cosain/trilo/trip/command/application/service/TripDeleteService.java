package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.usecase.TripDeleteUseCase;
import org.springframework.stereotype.Service;

@Service
public class TripDeleteService implements TripDeleteUseCase {


    @Override
    public void deleteTrip(Long tripId, Long tripperId) {
    }
}
