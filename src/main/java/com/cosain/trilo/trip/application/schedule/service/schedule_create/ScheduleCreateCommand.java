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

/**
 * 일정 생성에 필요한 명령(command, 비즈니스 입력 모델) 입니다.
 */
@Getter
@EqualsAndHashCode(of = {"requestTripperId", "tripId", "targetDayId", "scheduleTitle", "place"})
public class ScheduleCreateCommand {

    /**
     * 일정 생성을 시도하는 여행자(사용자)의 식별자(id)
     */
    private final long requestTripperId;

    /**
     * 여행의 식별자
     */
    private final long tripId;

    /**
     * Day의 식별자(null 일 경우 임시보관함)
     */
    private final Long targetDayId;

    /**
     * 일정의 제목
     */
    private final ScheduleTitle scheduleTitle;

    /**
     * 일정의 장소
     */
    private final Place place;

    /**
     * 일정 생성 명령(비즈니스 입력 모델)을 생성합니다.
     * @param requestTripperId 일정 생성을 시도하는 여행자(사용자)의 식별자(id)
     * @param tripId 여행의 식별자
     * @param targetDayId Day의 식별자(null 일 경우 임시보관함)
     * @param title 일정 제목의 원시값
     * @param placeId 장소의 식별자
     * @param placeName 장소명
     * @param latitude 위도
     * @param longitude 경도
     * @return 일정 생성 명령
     * @throws CustomValidationException 명령 생성과정에서 발생한 예외들을 묶은 예외
     */
    public static ScheduleCreateCommand of(long requestTripperId, long tripId, Long targetDayId, String title,
                                           String placeId, String placeName, Double latitude, Double longitude) throws CustomValidationException {

        List<CustomException> exceptions = new ArrayList<>(); // 발생 예외를 수집할 예외 수집기

        ScheduleTitle scheduleTitle = makeScheduleTitle(title, exceptions); // 일정 제목 생성 및 검증
        Coordinate coordinate = makeCoordinate(latitude, longitude, exceptions); // 일정 장소의 좌표 생성 및 검증
        Place place = makePlace(placeId, placeName, coordinate, exceptions); // 장소 생성

        if (!exceptions.isEmpty()) {
            // 예외가 하나라도 존재하면 이들을 모아서 검증 예외를 발생시킴
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
     * @param title 일정 제목의 원시값
     * @param exceptions 예외 수집기
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
     * @param latitude 위도
     * @param longitude 경도
     * @param exceptions 예외 수집기
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
     * @param placeId 장소 식별자
     * @param placeName 장소명
     * @param coordinate 좌표
     * @param exceptions 예외 수집기
     */
    private static Place makePlace(String placeId, String placeName, Coordinate coordinate, List<CustomException> exceptions) {
        try {
            return Place.of(placeId, placeName, coordinate);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleCreateCommand(long requestTripperId, long tripId, Long targetDayId, ScheduleTitle scheduleTitle, Place place) {
        this.requestTripperId = requestTripperId;
        this.tripId = tripId;
        this.targetDayId = targetDayId;
        this.scheduleTitle = scheduleTitle;
        this.place = place;
    }
}
