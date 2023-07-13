package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleTitleException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleTitle {

    private static final int MAX_LENGTH = 20;

    @Column(name = "schedule_title")
    private String value;

    public static ScheduleTitle of(String value) {
        if (value == null || value.length() > MAX_LENGTH) {
            throw new InvalidScheduleTitleException("null 또는 공백이거나, 길이 조건에 맞지 않는 여행 제목");
        }
        return new ScheduleTitle(value);
    }

    private ScheduleTitle(String value) {
        this.value = value;
    }
}
