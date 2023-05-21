package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.trip.query.service.dto.TripResult;

public interface TripDetailSearchUseCase {
    TripResult searchTripDetail(Long tripId, Long tripperId);
}
