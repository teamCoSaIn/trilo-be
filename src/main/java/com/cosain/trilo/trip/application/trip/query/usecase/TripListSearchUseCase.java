package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.infra.dto.TripDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TripListSearchUseCase {
    Slice<TripDetail> searchTripDetails(Long tripperId, Pageable pageable);
}
