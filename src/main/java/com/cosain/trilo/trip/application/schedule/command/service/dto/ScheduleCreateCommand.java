package com.cosain.trilo.trip.application.schedule.command.service.dto;

import com.cosain.trilo.trip.command.domain.vo.Coordinate;
import com.cosain.trilo.trip.command.domain.vo.Place;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleCreateCommand {

    private Long dayId;
    private Long tripId;

    private String title;
    private Place place;

    public static ScheduleCreateCommand of(Long dayId, Long tripId, String title,
                                           String placeId, String placeName, double latitude, double longitude) {
        return ScheduleCreateCommand.builder()
                .dayId(dayId)
                .tripId(tripId)
                .title(title)
                .place(Place.of(placeId, placeName, Coordinate.of(latitude, longitude)))
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleCreateCommand(Long dayId, Long tripId, String title, Place place) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.title = title;
        this.place = place;
    }
}
