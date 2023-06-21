package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.trip.command.usecase.TripImageUpdateUseCase;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TripImageUpdateService implements TripImageUpdateUseCase {

    private final TripRepository tripRepository;
    private final TripImageOutputAdapter tripImageOutputAdapter;

    @Override
    public String updateTripImage(Long tripId, Long tripperId, ImageFile file) {
        return null;
    }
}
