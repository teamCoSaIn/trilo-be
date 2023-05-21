package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.application.dto.TripResult;

public interface TripDetailSearchUseCase {
    TripResult searchTripDetail(Long tripId, Long tripperId);
}
