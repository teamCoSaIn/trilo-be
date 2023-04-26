package com.cosain.trilo.trip.command.domain.vo;

import com.cosain.trilo.trip.command.domain.exception.InvalidPeriodException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString(of = {"startDate", "endDate"})
@EqualsAndHashCode(of = {"startDate", "endDate"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripPeriod {

    private static final TripPeriod EMPTY_PERIOD = new TripPeriod(null, null);

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public static TripPeriod of(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return EMPTY_PERIOD;
        }
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new InvalidPeriodException("시작일 또는 종료일 어느 한 쪽만 null일 수 없습니다.");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidPeriodException("종료일이 시작일보다 앞섭니다.");
        }
        return new TripPeriod(startDate, endDate);
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
}
