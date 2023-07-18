package com.cosain.trilo.trip.application.dao;

import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripSummary;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPageCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.Optional;

public interface TripQueryDAO {

    Optional<TripDetail> findTripDetailById(Long tripId);
    Slice<TripSummary> findTripSummariesByTripperId(TripPageCondition tripPageCondition, Pageable pageable);
    boolean existById(Long tripId);
    TripStatistics findTripStaticsByTripperId(Long tripperId, LocalDate today);
}
