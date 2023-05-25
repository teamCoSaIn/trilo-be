package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidPeriodException;
import com.cosain.trilo.trip.domain.exception.TooLongPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Getter
@ToString(of = {"startDate", "endDate"})
@EqualsAndHashCode(of = {"startDate", "endDate"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripPeriod {

    private static final TripPeriod EMPTY_PERIOD = new TripPeriod(null, null);
    public static final int MAX_DAYS = 10;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public static TripPeriod of(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return EMPTY_PERIOD;
        }
        validateDateCombination(startDate, endDate);
        validateDayLength(startDate, endDate);
        return new TripPeriod(startDate, endDate);
    }

    private static void validateDateCombination(LocalDate startDate, LocalDate endDate) {
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new InvalidPeriodException("시작일 또는 종료일 어느 한 쪽만 null일 수 없습니다.");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidPeriodException("종료일이 시작일보다 앞섭니다.");
        }
    }

    private static void validateDayLength(LocalDate startDate, LocalDate endDate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // between은 날짜의 차를 구하므로, 사이에 속한 일수를 구하려면 1을 더해야함.
        if (numberOfDays > MAX_DAYS) {
            throw new TooLongPeriodException("여행 일수가 너무 많음.");
        }
    }

    private TripPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 기간이 없는 TripPeriod를 반환합니다.
     */
    public static TripPeriod empty() {
        return EMPTY_PERIOD;
    }

    /**
     * 겹치는 기간을 반환합니다.
     *
     * @param other
     * @return
     */
    public TripPeriod intersection(TripPeriod other) {
        return hasNoIntersection(other)
                ? empty()
                : calculateIntersectionPeriod(other);
    }

    private boolean hasNoIntersection(TripPeriod other) {
        return this.equals(empty()) || other.equals(empty()) || this.startDate.isAfter(other.endDate) || this.endDate.isBefore(other.startDate);
    }

    private TripPeriod calculateIntersectionPeriod(TripPeriod other) {
        LocalDate newStartDate = this.startDate.isAfter(other.startDate) ? this.startDate : other.startDate;
        LocalDate newEndDate = this.endDate.isBefore(other.endDate) ? this.endDate : other.endDate;
        return TripPeriod.of(newStartDate, newEndDate);
    }

    public boolean contains(LocalDate date) {
        return !this.equals(empty()) &&
                (date.isEqual(startDate) || (date.isAfter(startDate) && date.isBefore(endDate)) || date.isEqual(endDate));
    }

    /**
     * 해당 기간에 속하는 날짜들을 Stream으로 반환합니다.
     *
     * @return
     */
    public Stream<LocalDate> dateStream() {
        if (this.equals(EMPTY_PERIOD)) {
            return Stream.empty();
        }
        return Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }
}
