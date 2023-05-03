package com.cosain.trilo.trip.command.domain.vo;

import com.cosain.trilo.trip.command.domain.exception.ScheduleIndexRangeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleIndex {

    private static final long DEFAULT_SEQUENCE_GAP = 10_000_000;
    private static final long MAX_INDEX = 5_000_000_000_000_000_000L;
    private static final long MIN_INDEX = -5_000_000_000_000_000_000L;

    @Column(name = "schedule_index")
    private long value;

    public static final ScheduleIndex ZERO_INDEX = ScheduleIndex.of(0);

    public static ScheduleIndex of(long value) {
        if (value > MAX_INDEX || value < MIN_INDEX) {
            throw new ScheduleIndexRangeException("[처리 필요] 유효한 인덱스 범위를 벗어난 인덱스를 생성하려 함.");
        }
        return new ScheduleIndex(value);
    }

    private ScheduleIndex(long value) {
        this.value = value;
    }

}
