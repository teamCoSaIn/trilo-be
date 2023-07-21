package com.cosain.trilo.trip.application.schedule.service.schedule_create;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"requestTripperId", "tripId", "dayId", "scheduleTitle", "place"})
public class ScheduleCreateCommand {

    private final long requestTripperId;
    private final long tripId;
    private final Long targetDayId;
    private final ScheduleTitle scheduleTitle;
    private final Place place;

    public static ScheduleCreateCommand of(long requestTripperId, long tripId, Long targetDayId, String title,
                                           String placeId, String placeName, Double latitude, Double longitude) {
        List<CustomException> exceptions = new ArrayList<>();

        ScheduleTitle scheduleTitle = makeScheduleTitle(title, exceptions);
        Coordinate coordinate = makeCoordinate(latitude, longitude, exceptions);
        Place place = makePlace(placeId, placeName, coordinate, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }

        return ScheduleCreateCommand.builder()
                .requestTripperId(requestTripperId)
                .tripId(tripId)
                .targetDayId(targetDayId)
                .scheduleTitle(scheduleTitle)
                .place(place)
                .build();
    }


    /**
     * 일정 제목을 생성합니다. 일정 제목 정합성이 맞지 않을 경우 예외 목록에 추가합니다.
     */
    private static ScheduleTitle makeScheduleTitle(String title, List<CustomException> exceptions) {
        try {
            return ScheduleTitle.of(title);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    /**
     * 좌표를 생성합니다. 좌표 정합성이 맞지 않을 경우, 예외 목록에 추가합니다.
     */
    private static Coordinate makeCoordinate(Double latitude, Double longitude, List<CustomException> exceptions) {
        try {
            return Coordinate.of(latitude, longitude);
        } catch (CustomException e) {
            exceptions.add(e);
            return Coordinate.of(0.0, 0.0); // ...
        }
    }

    /**
     * 장소를 생성합니다. 정합성에 맞지 않으면 예외 목록에 추가합니다.
     */
    private static Place makePlace(String placeId, String placeName, Coordinate coordinate, List<CustomException> exceptions) {
        try {
            return Place.of(placeId, placeName, coordinate);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    @Builder(access = AccessLevel.PUBLIC)
    public ScheduleCreateCommand(long requestTripperId, long tripId, Long targetDayId, ScheduleTitle scheduleTitle, Place place) {
        this.requestTripperId = requestTripperId;
        this.tripId = tripId;
        this.targetDayId = targetDayId;
        this.scheduleTitle = scheduleTitle;
        this.place = place;
    }
}
