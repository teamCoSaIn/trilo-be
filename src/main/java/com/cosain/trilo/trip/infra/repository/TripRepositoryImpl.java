package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaTripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TripRepositoryImpl implements TripRepository {

    private final JpaTripRepository jpaTripRepository;

    @Override
    public Trip save(Trip trip) {
        return jpaTripRepository.save(trip);
    }

    @Override
    public Optional<Trip> findById(Long tripId) {
        return jpaTripRepository.findById(tripId);
    }

    @Override
    public Optional<Trip> findByIdWithDays(Long tripId) {
        return jpaTripRepository.findByIdWithDays(tripId);
    }

    @Override
    public List<Trip> findAllByTripperId(Long tripperId) {
        return jpaTripRepository.findAllByTripperId(tripperId);
    }

    @Override
    public void delete(Trip trip) {
        jpaTripRepository.delete(trip);
    }

    @Override
    public void deleteAllByTripperId(Long tripperId) {
        jpaTripRepository.deleteAllByTripperId(tripperId);
    }
}
