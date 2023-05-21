package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.trip.query.service.dto.TripPageResult;
import org.springframework.data.domain.Pageable;

public interface TripListSearchUseCase {
    TripPageResult searchTripDetails(Long tripperId, Pageable pageable);
}
