package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.application.trip.query.usecase.dto.TripResult;

public interface TripDetailSearchUseCase {
    TripResult searchTripDetail(Long tripId, Long tripperId);
}
