package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidPeriodException;
import com.cosain.trilo.trip.domain.exception.TooLongPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * 여행 기간을 정의한 VO(값 객체)입니다.
 */
@Getter
@ToString(of = {"startDate", "endDate"})
@EqualsAndHashCode(of = {"startDate", "endDate"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripPeriod {

    /**
     * 여행 기간이 정해지지 않았을 때의, 여행 기간
     *
     * @see TripStatus#UNDECIDED
     */
    private static final TripPeriod EMPTY_PERIOD = new TripPeriod(null, null);

    /**
     * 여행 기간의 최대 일수
     */
    public static final int MAX_DAYS = 10;

    /**
     * 여행의 시작일
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 여행의 종료일
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 여행기간 VO(값 객체)를 생성합니다.
     *
     * @param startDate 여행의 시작일
     * @param endDate   여행의 종료일
     * @return 여행기간 VO(값 객체)
     * @throws InvalidPeriodException 시작일 또는 종료일 어느 한 쪽이 null이거나, 종료일이 시작일보다 앞설 때
     * @throws TooLongPeriodException 여행기간의 일수가, 시스템에서 허용하는 최대 일수를 넘을 때
     */
    public static TripPeriod of(LocalDate startDate, LocalDate endDate) throws InvalidPeriodException, TooLongPeriodException {
        if (startDate == null && endDate == null) {
            // 시작일과 종료일이 모두 null 인 경우는 비즈니스 규칙적으로 허용함. (빈 기간)
            return EMPTY_PERIOD;
        }
        validateDateCombination(startDate, endDate);
        validateDayLength(startDate, endDate);
        return new TripPeriod(startDate, endDate);
    }

    /**
     * 여행 기간의 시작일, 종료일 조합의 유효성을 검증합니다.
     *
     * @param startDate 여행의 시작일
     * @param endDate   여행의 종료일
     * @throws InvalidPeriodException 시작일 또는 종료일 어느 한 쪽이 null이거나, 종료일이 시작일보다 앞설 때
     */
    private static void validateDateCombination(LocalDate startDate, LocalDate endDate) throws InvalidPeriodException {
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            // 어느 한 쪽만 null인 경우는 비즈니스 규칙적으로 허용하지 않음
            throw new InvalidPeriodException("시작일 또는 종료일 어느 한 쪽만 null일 수 없습니다.");
        }
        if (endDate.isBefore(startDate)) {
            // 종료일이 시작일보다 앞서는 경우는 비즈니스 규칙적으로 허용하지 않음
            throw new InvalidPeriodException("종료일이 시작일보다 앞섭니다.");
        }
    }

    /**
     * 여행기간의 일수가 유효한 지 검증합니다.
     *
     * @param startDate 여행의 시작일
     * @param endDate   여행의 종료일
     * @throws TooLongPeriodException 여행기간의 일수가, 시스템에서 허용하는 최대 일수를 넘을 때
     */
    private static void validateDayLength(LocalDate startDate, LocalDate endDate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // between은 날짜의 차를 구하므로, 사이에 속한 일수를 구하려면 1을 더해야함.
        if (numberOfDays > MAX_DAYS) {
            // 기간에 속한 여행 일수가, 최대 여행 일수보다 많은 경우는 비즈니스 규칙적으로 허용하지 않음
            throw new TooLongPeriodException("여행 일수가 너무 많음.");
        }
    }

    private TripPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 기간이 없는(시작일, 종료일 모두 없는) TripPeriod를 반환합니다.
     */
    public static TripPeriod empty() {
        return EMPTY_PERIOD;
    }

    /**
     * 전달받은 다른 여행 기간과, 겹치는 여행기간을 반환합니다.
     * @param other 다른 여행 기간
     * @return 겹치는 여행 기간
     */
    public TripPeriod intersection(TripPeriod other) {
        return hasNoIntersection(other)
                ? empty()
                : calculateIntersectionPeriod(other);
    }

    /**
     * 다른 여행기간과 겹치는 구간이 없는 지 여부를 반환합니다.
     * @param other : 다른 여행기간
     * @return 겹치는 구간이 없으면 true, 겹치는 구간이 있으면 false
     */
    private boolean hasNoIntersection(TripPeriod other) {
        return this.equals(EMPTY_PERIOD) // 자신이 빈 기간이거나
                || other.equals(EMPTY_PERIOD) // 다른 쪽이 빈 기간이거나
                || this.startDate.isAfter(other.endDate)  // 자신의 시작일이 반대쪽의 종료일 이후거나
                || this.endDate.isBefore(other.startDate); // 자신의 종료일이 반대쪽의 시작일 이전일 때
    }

    /**
     * 다른 여행기간과, 겹치는 여행기간을 반환합니다.(앞에서 겹치는 여행기간이 있음이 확인됐음을 전제)
     * @param other 다른 여행기간
     * @return 겹치는 여행기간
     */
    private TripPeriod calculateIntersectionPeriod(TripPeriod other) {
        LocalDate newStartDate = this.startDate.isAfter(other.startDate) ? this.startDate : other.startDate;
        LocalDate newEndDate = this.endDate.isBefore(other.endDate) ? this.endDate : other.endDate;
        return TripPeriod.of(newStartDate, newEndDate);
    }

    /**
     * 파라미터로 전달된 날짜가 여행기간에 속하는 지 여부를 반환합니다.
     * @param date 날짜
     * @return 해당 날짜가 여행 기간에 속하면 true, 속하지 않으면 false
     */
    public boolean contains(LocalDate date) {
        return !this.equals(EMPTY_PERIOD) // 자신이 비어있지 않은 기간이고
                &&
                // 파라미터의 date가 시작일이거나, 시작일 이후 이고 종료일 이전이거나, 종료일일 때
                (date.isEqual(startDate) || (date.isAfter(startDate) && date.isBefore(endDate)) || date.isEqual(endDate));
    }

    /**
     * 여행기간에 속하는 날짜들을 Stream으로 반환합니다.
     * @return 기간에 속하는 날짜들의 Stream
     * @see Stream
     */
    public Stream<LocalDate> dateStream() {
        if (this.equals(EMPTY_PERIOD)) {
            return Stream.empty();
        }
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }
}
