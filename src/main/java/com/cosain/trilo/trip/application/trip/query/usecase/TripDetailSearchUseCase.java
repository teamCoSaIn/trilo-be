package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.infra.dto.TripDetail;

public interface TripDetailSearchUseCase {
    TripDetail searchTripDetail(Long tripId, Long tripperId);
}
