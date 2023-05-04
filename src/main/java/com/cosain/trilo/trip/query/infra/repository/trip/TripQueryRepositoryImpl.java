package com.cosain.trilo.trip.query.infra.repository.trip;

import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TripQueryRepositoryImpl implements TripQueryRepository {

    private final TripQueryDslRepository tripQueryDslRepository;

    @Override
    public Optional<TripDetail> findTripDetailByTripId(Long tripId) {
        return tripQueryDslRepository.findTripDetailById(tripId);
    }
}
