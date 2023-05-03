package com.cosain.trilo.trip.query.domain.repository;

import com.cosain.trilo.trip.query.infra.dto.TripDetail;

import java.util.Optional;

public interface TripQueryRepository {
    Optional<TripDetail> findTripDetailByTripId(Long tripId);
}
