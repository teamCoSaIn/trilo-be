package com.cosain.trilo.trip.command.application.usecase;

public interface TripDeleteUseCase {
    void deleteTrip(Long tripId, Long tripperId);
}
