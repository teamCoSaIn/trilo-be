package com.cosain.trilo.trip.infra.dao;

import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripSummary;
import com.cosain.trilo.trip.infra.dao.querydsl.QuerydslTripQueryRepository;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPageCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TripQueryDAOImpl implements TripQueryDAO {

    private final QuerydslTripQueryRepository querydslTripQueryRepository;

    public Optional<TripDetail> findTripDetailById(Long tripId) {
        return querydslTripQueryRepository.findTripDetailById(tripId);
    }

    @Override
    public Slice<TripSummary> findTripSummariesByTripperId(TripPageCondition tripPageCondition, Pageable pageable) {
        return querydslTripQueryRepository.findTripSummariesByTripperId(tripPageCondition, pageable);
    }

    @Override
    public boolean existById(Long tripId) {
        return querydslTripQueryRepository.existById(tripId);
    }

    @Override
    public TripStatistics findTripStaticsByTripperId(Long tripperId, LocalDate today) {
        return querydslTripQueryRepository.findTripStaticsByTripperId(tripperId, today);
    }
}
