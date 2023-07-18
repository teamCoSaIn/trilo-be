package com.cosain.trilo.trip.application.schedule.service.schedule_create;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.exception.NullTripIdException;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleCreateCommandFactory {

    public ScheduleCreateCommand createCommand(
            Long dayId, Long tripId, String title,
            String placeId, String placeName,
            Double latitude, Double longitude, List<CustomException> exceptions) {
        validateTripIdNotNull(tripId, exceptions);

        ScheduleTitle scheduleTitle = makeScheduleTitle(title, exceptions);
        Coordinate coordinate = makeCoordinate(latitude, longitude, exceptions);
        Place place = makePlace(placeId, placeName, coordinate, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }

        return ScheduleCreateCommand.builder()
                .dayId(dayId)
                .tripId(tripId)
                .scheduleTitle(scheduleTitle)
                .place(place)
                .build();
    }

    /**
     * TridId가 Null이 아닌지 검증합니다. null이면, 예외 목록에 추가합니다.
     */
    private void validateTripIdNotNull(Long tripId, List<CustomException> exceptions) {
        if (tripId == null) {
            exceptions.add(new NullTripIdException("Trip의 식별자가 전달되지 않음"));
        }
    }

    /**
     * 일정 제목을 생성합니다. 일정 제목 정합성이 맞지 않을 경우 예외 목록에 추가합니다.
     */
    private ScheduleTitle makeScheduleTitle(String title, List<CustomException> exceptions) {
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
    private Coordinate makeCoordinate(Double latitude, Double longitude, List<CustomException> exceptions) {
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
    private Place makePlace(String placeId, String placeName, Coordinate coordinate, List<CustomException> exceptions) {
        try {
            return Place.of(placeId, placeName, coordinate);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }
}
