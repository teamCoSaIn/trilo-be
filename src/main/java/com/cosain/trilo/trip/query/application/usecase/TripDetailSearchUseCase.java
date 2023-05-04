package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.presentation.trip.dto.TripDetailResponse;

public interface TripDetailSearchUseCase {
    TripDetailResponse searchTripDetail(Long tripId, Long tripperId);
}
