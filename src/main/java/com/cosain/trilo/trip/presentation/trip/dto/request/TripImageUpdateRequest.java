package com.cosain.trilo.trip.presentation.trip.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class TripImageUpdateRequest {

    @NotNull(message = "file-0001")
    private final MultipartFile image;

    public TripImageUpdateRequest(MultipartFile image) {
        this.image = image;
    }
}
