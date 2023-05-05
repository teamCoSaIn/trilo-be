package com.cosain.trilo.trip.query.infra.repository.trip;

import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.infra.repository.trip.jpa.TripQueryJpaRepository;
import lombok.RequiredArgsConstructor;
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
}
