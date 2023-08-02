package com.cosain.trilo.trip.presentation.schedule.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일정 생성을 위한 요청 정보를 이 객체에 바인딩합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateRequest {

    /**
     * Day의 식별자(null 일 경우 임시보관함)
     */
    private Long dayId;

    /**
     * 여행의 식별자
     */
    @NotNull(message = "schedule-0007")
    private Long tripId;

    /**
     * 일정의 제목
     */
    private String title;

    /**
     * 장소 식별자
     */
    private String placeId;

    /**
     * 장소명
     */
    private String placeName;

    /**
     * 좌표({@link CoordinateDto )
     */
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

    /**
     * 일정 생성 요청으로 전달된 좌표를 바인딩하는 객체입니다.
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CoordinateDto {

        /**
         * 위도
         */
        private Double latitude;

        /**
         * 경도
         */
        private Double longitude;

        public CoordinateDto(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
