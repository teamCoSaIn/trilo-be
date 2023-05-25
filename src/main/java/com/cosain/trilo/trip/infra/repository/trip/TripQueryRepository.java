package com.cosain.trilo.trip.infra.repository.trip;

import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface TripQueryRepository {
    Optional<TripDetail> findTripDetailByTripId(Long tripId);

    Slice<TripSummary> findTripSummariesByTripperId(Long tripperId, Pageable pageable);

    boolean existById(Long tripI);
}
