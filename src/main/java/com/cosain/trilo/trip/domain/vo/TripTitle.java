package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.common.exception.trip.InvalidTripTitleException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 여행제목 VO(값 객체)입니다.
 */
@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripTitle {

    /**
     * 여행 제목의 최소 길이 제한입니다.
     */
    private static final int MIN_LENGTH = 1;

    /**
     * 여행 제목의 최대 길이 제한입니다.
     */
    private static final int MAX_LENGTH = 20;

    /**
     * 여행제목의 원시 문자열 값입니다.
     */
    @Column(name = "trip_title")
    private String value;

    /**
     * 여행제목 VO(값 객체)를 생성합니다.
     * @param value : 여행 제목 문자열
     * @return 여행 제목 VO(값 객체)
     * @throws InvalidTripTitleException : 여행 제목이 유효하지 않을 때
     */
    public static TripTitle of(String value) throws InvalidTripTitleException {
        if (isNullOrBlank(value) || hasInvalidLength(value)) {
            throw new InvalidTripTitleException("null 또는 공백이거나, 길이 조건에 맞지 않는 여행 제목");
        }
        return new TripTitle(value);
    }

    /**
     * 여행 제목이 null 또는 공백으로만 구성됐는 지 여부를 반환합니다.
     * @param value : 여행 제목 문자열
     * @return 여행 제목이 null 이거나 공백으로만 구성되면 true, 아니면 false
     */
    private static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * 여행 제목이 유효하지 않은 길이를 가졌는 지 여부를 반환합니다.
     * @param value : 여행 제목
     * @return 여행 제목 길이가 유효하지 않으면 true, 유효하면 false
     */
    private static boolean hasInvalidLength(String value) {
        return value.length() < MIN_LENGTH || value.length() > MAX_LENGTH;
    }

    private TripTitle(String value) {
        this.value = value;
    }
}
