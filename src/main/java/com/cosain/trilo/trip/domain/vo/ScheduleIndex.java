package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.common.exception.schedule.ScheduleIndexRangeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigInteger;

/**
 * <p>일정의 드래그 앤 드롭을 원활하게 구현하기 위한 값입니다.</p>
 * <p>작을수록 임시보관함 또는 Day 내에서 앞에 위치하게 됩니다.</p>
 * <p>클수록 임시보관함 또는 Day 내에서 뒤에 위치하게 됩니다.</p>
 */
@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleIndex {

    /**
     * 기존 Day 또는 임시보관함 맨 앞, 맨 뒤에 새로 생성할 때마다, 벌려지는 기본 사이 간격
     */
    public static final long DEFAULT_SEQUENCE_GAP = 10_000_000;

    /**
     * 허용하는 최대 ScheduleIndex 값
     */
    public static final long MAX_INDEX_VALUE = 5_000_000_000_000_000_000L;

    /**
     * 허용하는 최소 ScheduleIndex 값
     */
    public static final long MIN_INDEX_VALUE = -5_000_000_000_000_000_000L;

    /**
     * ScheduleIndex의 실제 값
     */
    @Column(name = "schedule_index")
    private long value;

    /**
     * 자주 사용되는 0 ScheduleIndex를 캐싱
     */
    public static final ScheduleIndex ZERO_INDEX = ScheduleIndex.of(0);

    /**
     * 지정한 값의 ScheduleIndex 생성
     * @param value ScheduleIndex 값
     * @return ScheduleIndex 생성되는 ScheduleIndex
     * @throws ScheduleIndexRangeException 유효한 인덱스 범위를 벗어날 때
     */
    public static ScheduleIndex of(long value) throws ScheduleIndexRangeException {
        if (value > MAX_INDEX_VALUE || value < MIN_INDEX_VALUE) {
            throw new ScheduleIndexRangeException("[처리 필요] 유효한 인덱스 범위를 벗어난 인덱스를 생성하려 함.");
        }
        return new ScheduleIndex(value);
    }

    private ScheduleIndex(long value) {
        this.value = value;
    }

    /**
     * 자기 자신 다음 위치에 해당하는 ScheduleIndex 를 생성
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    public ScheduleIndex generateNextIndex() throws ScheduleIndexRangeException {
        return ScheduleIndex.of(value + DEFAULT_SEQUENCE_GAP);
    }

    /**
     * 자기 자신의 이전에 해당하는 ScheduleIndex 생성
     * @return 앞에 오는 인덱스
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    public ScheduleIndex generateBeforeIndex() throws ScheduleIndexRangeException {
        return ScheduleIndex.of(value - DEFAULT_SEQUENCE_GAP);
    }

    /**
     * 중간 인덱스 생성
     * @param other : 다른 대상 인덱스
     * @return : 자기 자신과 다른 대상 ScheduleIndex 의 중간 위치에 대응되는 ScheduleIndex
     */
    public ScheduleIndex mid(ScheduleIndex other) {
        // long 범위를 벗어날 위험이 있어서, BigInteger로 계산
        BigInteger value1 = BigInteger.valueOf(this.value);
        BigInteger value2 = BigInteger.valueOf(other.value);

        long midValue = value1.add(value2).divide(BigInteger.TWO).longValue();
        return new ScheduleIndex(midValue);
    }

}
