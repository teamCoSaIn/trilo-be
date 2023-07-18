package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DayRepositoryImpl implements DayRepository {

    private final JpaDayRepository jpaDayRepository;

    @Override
    public void saveAll(List<Day> days) {
        jpaDayRepository.saveAll(days);
    }

    @Override
    public Optional<Day> findByIdWithTrip(Long dayId) {
        return jpaDayRepository.findByIdWithTrip(dayId);
    }

    @Override
    public int deleteAllByIds(List<Long> dayIds) {
        return jpaDayRepository.deleteAllByIds(dayIds);
    }

    @Override
    public void deleteAllByTripId(Long tripId) {
        jpaDayRepository.deleteAllByTripId(tripId);
    }

    @Override
    public void deleteAllByTripIds(List<Long> tripIds) {
        jpaDayRepository.deleteAllByTripIds(tripIds);
    }
}
