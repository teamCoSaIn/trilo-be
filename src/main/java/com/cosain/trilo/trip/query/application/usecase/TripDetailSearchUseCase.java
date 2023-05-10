package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.application.dto.TripResult;
import com.cosain.trilo.trip.query.presentation.trip.dto.TripDetailResponse;

public interface TripDetailSearchUseCase {
    TripResult searchTripDetail(Long tripId, Long tripperId);
}
