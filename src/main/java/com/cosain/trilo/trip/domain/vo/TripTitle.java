package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.common.exception.trip.InvalidTripTitleException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripTitle {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 20;

    @Column(name = "trip_title")
    private String value;

    public static TripTitle of(String value) {
        if (isNullOrBlank(value) || hasInvalidLength(value)) {
            throw new InvalidTripTitleException("null 또는 공백이거나, 길이 조건에 맞지 않는 여행 제목");
        }
        return new TripTitle(value);
    }

    private static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean hasInvalidLength(String value) {
        return value.length() < MIN_LENGTH || value.length() > MAX_LENGTH;
    }

    private TripTitle(String value) {
        this.value = value;
    }
}
