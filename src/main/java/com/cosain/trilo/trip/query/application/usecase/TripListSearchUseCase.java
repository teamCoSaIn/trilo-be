package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.presentation.trip.dto.TripPageResponse;
import org.springframework.data.domain.Pageable;

public interface TripListSearchUseCase {
    TripPageResponse searchTripDetails(Long tripperId, Pageable pageable);
}
