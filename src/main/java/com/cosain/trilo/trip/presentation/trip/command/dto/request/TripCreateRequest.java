package com.cosain.trilo.trip.presentation.trip.command.dto.request;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripCreateRequest {

    @NotBlank(message = "trip-0002")
    @Size(min = 1, max = 20, message = "trip-0002")
    private String title;

    public TripCreateRequest(String title) {
        this.title = title;
    }

    public TripCreateCommand toCommand() {
        return TripCreateCommand.from(title);
    }
}
