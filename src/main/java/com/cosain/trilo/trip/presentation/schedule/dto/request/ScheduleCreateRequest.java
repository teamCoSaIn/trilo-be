package com.cosain.trilo.trip.presentation.schedule.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateRequest {

    private Long dayId;

    @NotNull(message = "schedule-0007")
    private Long tripId;

    private String title;
    private String placeId;
    private String placeName;

    @NotNull(message = "place-0002")
    private CoordinateDto coordinate;

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private ScheduleCreateRequest(Long dayId, Long tripId, String title, String placeId, String placeName, CoordinateDto coordinate) {
        this.dayId = dayId;
        this.tripId = tripId;
        this.title = title;
        this.placeId = placeId;
        this.placeName = placeName;
        this.coordinate = coordinate;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CoordinateDto {

        private Double latitude;
        private Double longitude;

        public CoordinateDto(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
