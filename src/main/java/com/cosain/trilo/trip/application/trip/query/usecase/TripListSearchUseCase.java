package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.application.trip.query.usecase.dto.TripPageResult;
import org.springframework.data.domain.Pageable;

public interface TripListSearchUseCase {
    TripPageResult searchTripDetails(Long tripperId, Pageable pageable);
}
