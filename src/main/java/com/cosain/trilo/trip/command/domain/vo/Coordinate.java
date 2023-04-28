package com.cosain.trilo.trip.command.domain.vo;

import com.cosain.trilo.trip.command.domain.exception.InvalidCoordinateException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"latitude", "longitude"})
@ToString(of = {"latitude", "longitude"})
@Embeddable
public class Coordinate {

    private static final double MIN_LATITUDE = -90;
    private static final double MAX_LATITUDE = 90;

    private static final double MIN_LONGITUDE = -180;
    private static final double MAX_LONGITUDE = 180;

    @Column(name = "place_latitude", nullable = false)
    private double latitude;

    @Column(name= "place_longitude", nullable = false)
    private double longitude;

    public static Coordinate of(double latitude, double longitude) {
        if (!(MIN_LATITUDE <= latitude && latitude <= MAX_LATITUDE)) {
            throw new InvalidCoordinateException("위도의 범위가 옳지 않음");
        }
        if (!(MIN_LATITUDE <= longitude && longitude <= MAX_LONGITUDE)) {
            throw new InvalidCoordinateException("경도의 범위가 옳지 않음");
        }
        return new Coordinate(latitude, longitude);
    }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
