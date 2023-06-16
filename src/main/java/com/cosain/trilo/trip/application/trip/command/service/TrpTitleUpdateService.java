package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.usecase.TripTitleUpdateUseCase;
import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripTitleUpdateCommand;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrpTitleUpdateService implements TripTitleUpdateUseCase {

    private final TripRepository tripRepository;

    @Override
    public void updateTripTitle(Long tripId, Long tripperId, TripTitleUpdateCommand command) {
    }
}
