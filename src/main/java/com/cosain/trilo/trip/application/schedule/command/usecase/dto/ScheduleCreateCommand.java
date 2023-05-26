package com.cosain.trilo.trip.application.schedule.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ScheduleCreateCommand {

    private Long dayId;
    private Long tripId;
    private ScheduleTitle scheduleTitle;
    private Place place;

    public static ScheduleCreateCommand of(Long dayId, Long tripId, String title,
                                           String placeId, String placeName, double latitude, double longitude) {

        return ScheduleCreateCommand.builder()
                .dayId(dayId)
                .tripId(tripId)
                .scheduleTitle(ScheduleTitle.of(title))
                .place(Place.of(placeId, placeName, Coordinate.of(latitude, longitude)))
                .build();
    }

    @Builder(access = AccessLevel.PUBLIC)
    private ScheduleCreateCommand(Long dayId, Long tripId, ScheduleTitle scheduleTitle, Place place) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.scheduleTitle = scheduleTitle;
        this.place = place;
    }

    /**
     * 임시로 남겨둠. 이후 지울 예정
     */
    public String getTitle() {
        return scheduleTitle.getValue();
    }
}
