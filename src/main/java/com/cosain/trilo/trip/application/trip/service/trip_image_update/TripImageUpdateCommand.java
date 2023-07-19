package com.cosain.trilo.trip.application.trip.service.trip_image_update;

import com.cosain.trilo.common.file.ImageFile;
import lombok.Getter;

@Getter
public class TripImageUpdateCommand {

    private final long tripId;
    private final long requestTripperId;
    private final ImageFile imageFile;

    public TripImageUpdateCommand(long tripId, long requestTripperId, ImageFile imageFile) {
        this.tripId = tripId;
        this.requestTripperId = requestTripperId;
        this.imageFile = imageFile;
    }
}
