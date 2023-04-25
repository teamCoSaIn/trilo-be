package com.cosain.trilo.trip.command.domain.vo;

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

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public static TripPeriod of(LocalDate startDate, LocalDate endDate){
        //TODO: TripPeriod 생성 검증로직 추가 -> 도메인 규칙 방어
        return new TripPeriod(startDate, endDate);
    }

    private TripPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
