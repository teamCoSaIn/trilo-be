package com.cosain.trilo.trip.command.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString(of = {"id", "tripDate"})
@Entity
@Table(name = "days")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long id;

    @Column(name = "trip_date")
    private LocalDate tripDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    /**
     * 비즈니스 코드에서 Day 생성은 Trip 에서만 할 수 있다.
     */
    public static Day of(LocalDate date, Trip trip){
        return new Day(date, trip);
    }

    private Day(LocalDate date, Trip trip) {
        this.tripDate = date;
        this.trip = trip;
    }

}
