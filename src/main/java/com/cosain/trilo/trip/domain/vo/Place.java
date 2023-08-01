package com.cosain.trilo.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

/**
 * 일정의 장소를 나타내는 VO(값 객체)입니다.
 */
@Getter
@ToString(of = {"placeId", "placeName", "coordinate"})
@EqualsAndHashCode(of = {"placeId", "placeName", "coordinate"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Place {

    /**
     * 장소 식별자(지도 API를 통해 전달받은 값)
     */
    @Column(name = "place_id", nullable = true)
    private String placeId;

    /**
     * 장소명
     */
    @Column(name = "place_name", nullable = false)
    private String placeName;

    /**
     * 좌표
     */
    @Embedded
    private Coordinate coordinate;

    /**
     * 일정의 장소를 생성합니다.
     * @param placeId 장소 식별자(id)
     * @param placeName 장소명
     * @param coordinate 좌표
     * @return 장소
     */
    public static Place of(String placeId, String placeName, Coordinate coordinate) {
        return Place.builder()
                .placeId(placeId)
                .placeName(placeName)
                .coordinate(coordinate)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Place(String placeId, String placeName, Coordinate coordinate) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.coordinate = coordinate;
    }
}
