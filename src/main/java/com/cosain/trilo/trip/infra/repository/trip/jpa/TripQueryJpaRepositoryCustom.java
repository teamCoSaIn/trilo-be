package com.cosain.trilo.trip.infra.repository.trip.jpa;

import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface TripQueryJpaRepositoryCustom {
    Optional<TripDetail> findTripDetailById(Long tripId);

    Slice<TripSummary> findTripSummariesByTripperId(Long tripperId, Pageable pageable);
}
