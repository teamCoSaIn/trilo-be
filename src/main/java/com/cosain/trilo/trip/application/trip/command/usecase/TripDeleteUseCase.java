package com.cosain.trilo.trip.application.trip.command.usecase;

public interface TripDeleteUseCase {
    void deleteTrip(Long tripId, Long tripperId);
}
