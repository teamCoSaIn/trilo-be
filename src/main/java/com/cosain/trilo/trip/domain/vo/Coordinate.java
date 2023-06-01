package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidCoordinateException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"latitude", "longitude"})
@ToString(of = {"latitude", "longitude"})
@Embeddable
public class Coordinate {

    public static final double MIN_LATITUDE = -90;
    public static final double MAX_LATITUDE = 90;

    public static final double MIN_LONGITUDE = -180;
    public static final double MAX_LONGITUDE = 180;

    @Column(name = "place_latitude", nullable = false)
    private double latitude;

    @Column(name= "place_longitude", nullable = false)
    private double longitude;

    public static Coordinate of(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new InvalidCoordinateException("위도 또는 경도가 누락됨");
        }
        if (!(MIN_LATITUDE <= latitude && latitude <= MAX_LATITUDE)) {
            throw new InvalidCoordinateException("위도의 범위가 옳지 않음");
        }
        if (!(MIN_LATITUDE <= longitude && longitude <= MAX_LONGITUDE)) {
            throw new InvalidCoordinateException("경도의 범위가 옳지 않음");
        }
        return new Coordinate(latitude, longitude);
    }

    private Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
