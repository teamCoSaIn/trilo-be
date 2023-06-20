package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TripListSearchUseCase {
    Slice<TripSummary> searchTripSummaries(TripPageCondition tripPageCondition, Pageable pageable);
}
