package com.cosain.trilo.trip.application.trip.command.service;

public interface TripDeleteUseCase {
    void deleteTrip(Long tripId, Long tripperId);
}
