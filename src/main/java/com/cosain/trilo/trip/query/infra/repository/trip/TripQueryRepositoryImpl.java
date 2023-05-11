package com.cosain.trilo.trip.query.infra.repository.trip;

import com.cosain.trilo.trip.query.domain.dto.TripDto;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.cosain.trilo.trip.query.infra.repository.trip.jpa.TripQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TripQueryRepositoryImpl implements TripQueryRepository {

    private final TripQueryJpaRepository tripQueryJpaRepository;

    @Override
    public Optional<TripDto> findTripDetailByTripId(Long tripId) {
        Optional<TripDetail> tripDetail = tripQueryJpaRepository.findTripDetailById(tripId);
        return tripDetail.isEmpty() ? Optional.empty() : Optional.of(TripDto.from(tripDetail.get()));
    }

    @Override
    public Slice<TripDto> findTripDetailListByTripperId(Long tripperId, Pageable pageable) {
        Slice<TripDetail> tripDetails = tripQueryJpaRepository.findTripDetailListByTripperId(tripperId, pageable);
        List<TripDto> tripDtos = tripDetails.map(TripDto::from).getContent();
        return new SliceImpl<>(tripDtos, pageable, tripDetails.hasNext());
    }
}
