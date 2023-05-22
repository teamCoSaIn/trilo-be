package com.cosain.trilo.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@Getter
@ToString(of = {"placeId", "placeName", "coordinate"})
@EqualsAndHashCode(of = {"placeId", "placeName", "coordinate"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Place {

    @Column(name = "place_id", nullable = true)
    private String placeId;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Embedded
    private Coordinate coordinate;

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
