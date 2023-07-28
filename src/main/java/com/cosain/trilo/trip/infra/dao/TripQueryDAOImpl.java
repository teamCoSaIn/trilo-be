package com.cosain.trilo.trip.infra.dao;

import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.infra.dao.querydsl.QuerydslTripQueryRepository;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import lombok.RequiredArgsConstructor;
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
    public TripListSearchResult findTripSummariesByTripperId(TripListQueryParam queryParam) {
        return querydslTripQueryRepository.findTripSummariesByTripperId(queryParam);
    }

    @Override
    public boolean existById(Long tripId) {
        return querydslTripQueryRepository.existById(tripId);
    }

    @Override
    public TripStatistics findTripStaticsByTripperId(Long tripperId, LocalDate today) {
        return querydslTripQueryRepository.findTripStaticsByTripperId(tripperId, today);
    }
    @Override
    public TripSearchResponse findWithSearchConditions(TripSearchRequest request) {
        return querydslTripQueryRepository.findTripWithSearchCondition(request);
    }
}
