package com.cosain.trilo.trip.infra.repository.trip;

import com.cosain.trilo.trip.infra.dto.TripDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface TripQueryRepository {
    Optional<TripDetail> findTripDetailByTripId(Long tripId);

    Slice<TripDetail> findTripDetailListByTripperId(Long tripperId, Pageable pageable);

    boolean existById(Long tripI);
}
