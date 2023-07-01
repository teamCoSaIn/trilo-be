package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripAllDeleteService {

    private final TripRepository tripRepository;
    private final DayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;

    public void deleteAllByTripperId(Long tripperId){
        List<Trip> trips = tripRepository.findAllByTripperId(tripperId);
        List<Long> tripIdsForDelete = trips.stream().map(Trip::getId).collect(Collectors.toList());
        scheduleRepository.deleteAllByTripIds(tripIdsForDelete);
        dayRepository.deleteAllByTripIds(tripIdsForDelete);
        tripRepository.deleteAllByTripperId(tripperId);
    }
}
