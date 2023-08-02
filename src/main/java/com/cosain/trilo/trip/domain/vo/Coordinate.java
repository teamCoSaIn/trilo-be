package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.common.exception.place.InvalidCoordinateException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 일정에 속한 장소({@link Place})의 좌표를 나타내는 VO(값 객체)입니다.
 * @see Place
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"latitude", "longitude"})
@ToString(of = {"latitude", "longitude"})
@Embeddable
public class Coordinate {

    /**
     * 위도의 최솟값
     */
    public static final double MIN_LATITUDE = -90;

    /**
     * 위도의 최댓값
     */
    public static final double MAX_LATITUDE = 90;

    /**
     * 경도의 최솟값
     */
    public static final double MIN_LONGITUDE = -180;

    /**
     * 경도의 최댓값
     */
    public static final double MAX_LONGITUDE = 180;

    /**
     * 위도
     */
    @Column(name = "place_latitude", nullable = false)
    private double latitude;

    /**
     * 경도
     */
    @Column(name = "place_longitude", nullable = false)
    private double longitude;

    /**
     * 일정이 일어나는 장소의 좌표를 생성합니다.
     * @param latitude  위도
     * @param longitude 경도
     * @return 좌표
     * @throws InvalidCoordinateException 좌표의 위도,경도가 유효하지 않을 때
     */
    public static Coordinate of(Double latitude, Double longitude) throws InvalidCoordinateException {
        validateBothNotNull(latitude, longitude); // 둘 중 하나라도 null이여선 안 됨
        validateLatitude(latitude); // 위도 검증
        validateLongitude(longitude); // 경도 검증

        return new Coordinate(latitude, longitude);
    }

    /**
     * 위도, 경도 둘 중 어느 하나라도 null이면 예외를 발생시킵니다.
     * @param latitude 위도
     * @param longitude 경도
     * @throws InvalidCoordinateException 위도, 경도 둘 중 하나라도 null
     */
    private static void validateBothNotNull(Double latitude, Double longitude) throws InvalidCoordinateException {
        if (latitude == null || longitude == null) {
            throw new InvalidCoordinateException("위도 또는 경도가 누락됨");
        }
    }

    /**
     * 위도값이 유효하지 않으면 예외를 발생시킵니다.
     * @param latitude 위도
     * @throws InvalidCoordinateException 위도값이 유효하지 않을 때
     */
    private static void validateLatitude(Double latitude) throws InvalidCoordinateException {
        if (!(MIN_LATITUDE <= latitude && latitude <= MAX_LATITUDE)) {
            throw new InvalidCoordinateException("위도의 범위가 옳지 않음");
        }
    }

    /**
     * 경도값이 유효하지 않으면 예외를 발생시킵니다.
     * @param longitude 경도
     * @throws InvalidCoordinateException 경도값이 유효하지 않을 때
     */
    private static void validateLongitude(Double longitude) throws InvalidCoordinateException {
        if (!(MIN_LONGITUDE <= longitude && longitude <= MAX_LONGITUDE)) {
            throw new InvalidCoordinateException("경도의 범위가 옳지 않음");
        }
    }

    private Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
