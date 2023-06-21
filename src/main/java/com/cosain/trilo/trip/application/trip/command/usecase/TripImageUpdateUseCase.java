package com.cosain.trilo.trip.application.trip.command.usecase;

import com.cosain.trilo.common.file.ImageFile;

public interface TripImageUpdateUseCase {

    String updateTripImage(Long tripId, Long tripperId, ImageFile file);

}
