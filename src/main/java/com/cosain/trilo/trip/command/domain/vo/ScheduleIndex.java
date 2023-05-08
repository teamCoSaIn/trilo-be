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

    public static final long DEFAULT_SEQUENCE_GAP = 10_000_000;
    public static final long MAX_INDEX_VALUE = 5_000_000_000_000_000_000L;
    public static final long MIN_INDEX_VALUE = -5_000_000_000_000_000_000L;

    @Column(name = "schedule_index")
    private long value;

    public static final ScheduleIndex ZERO_INDEX = ScheduleIndex.of(0);

    public static ScheduleIndex of(long value) {
        if (value > MAX_INDEX_VALUE || value < MIN_INDEX_VALUE) {
            throw new ScheduleIndexRangeException("[처리 필요] 유효한 인덱스 범위를 벗어난 인덱스를 생성하려 함.");
        }
        return new ScheduleIndex(value);
    }

    private ScheduleIndex(long value) {
        this.value = value;
    }

    public ScheduleIndex generateNextIndex() {
        return ScheduleIndex.of(value + DEFAULT_SEQUENCE_GAP);
    }


    /**
     * 자기 자신의 앞에 새로 인덱스를 생성합니다.
     * @return 앞에 오는 인덱스
     */
    public ScheduleIndex generateBeforeIndex() {
        return ScheduleIndex.of(value - DEFAULT_SEQUENCE_GAP);
    }

}
