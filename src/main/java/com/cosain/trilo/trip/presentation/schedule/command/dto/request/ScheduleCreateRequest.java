package com.cosain.trilo.trip.presentation.schedule.command.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateRequest {

    private Long dayId;
    private Long tripId;
    private String title;

    private String placeId;
    private String placeName;
    private RequestCoordinate coordinate;

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private ScheduleCreateRequest(Long dayId, Long tripId, String title, String placeId, String placeName, RequestCoordinate coordinate) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.title = title;
        this.placeId = placeId;
        this.placeName = placeName;
        this.coordinate = coordinate;
    }

}
