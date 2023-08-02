package com.cosain.trilo.trip.application.trip.service.trip_like;

@FunctionalInterface
public interface TripLikeOperation {
    void perform(Long tripId, Long tripperId);
}
