package com.cosain.trilo.trip.infra.repository.trip;

import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.repository.trip.jpa.TripQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TripQueryRepositoryImpl implements TripQueryRepository {

    private final TripQueryJpaRepository tripQueryJpaRepository;

    @Override
    public Optional<TripDetail> findTripDetailByTripId(Long tripId) {
        return tripQueryJpaRepository.findTripDetailById(tripId);
    }

    @Override
    public Slice<TripDetail> findTripDetailListByTripperId(Long tripperId, Pageable pageable) {
        return tripQueryJpaRepository.findTripDetailListByTripperId(tripperId, pageable);
    }

    @Override
    public boolean existById(Long tripId) {
        return tripQueryJpaRepository.existsById(tripId);
    }
}
