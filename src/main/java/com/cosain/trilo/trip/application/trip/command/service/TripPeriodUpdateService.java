package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.usecase.TripPeriodUpdateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripPeriodUpdateService implements TripPeriodUpdateUseCase {

    private final TripRepository tripRepository;
    private final DayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public void updateTripPeriod(Long tripId, Long tripperId, TripPeriodUpdateCommand updateCommand) {
    }
}
