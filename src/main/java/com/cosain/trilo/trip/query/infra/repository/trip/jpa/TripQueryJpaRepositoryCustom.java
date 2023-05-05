package com.cosain.trilo.trip.query.infra.repository.trip.jpa;

import com.cosain.trilo.trip.query.infra.dto.TripDetail;

import java.util.Optional;

public interface TripQueryJpaRepositoryCustom {
    Optional<TripDetail> findTripDetailById(Long tripId);
}
