package com.cosain.trilo.fixture;

import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

public enum TripFixture {

    UNDECIDED_TRIP(TripStatus.UNDECIDED),
    DECIDED_TRIP(TripStatus.DECIDED),
    ;

    private final TripStatus status;
    TripFixture(TripStatus status){
        this.status = status;
    }

    public Trip createUndecided(Long id, Long tripperId, String title){
        if(this.status.equals(TripStatus.DECIDED)) throw new IllegalArgumentException("status 가 DECIDED 일 경우 startDate와 endDate를 지정해주어야 합니다.");
        return Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .title(title)
                .status(this.status)
                .build();
    }

    public Trip createDecided(Long id, Long tripperId, String title, LocalDate startDate, LocalDate endDate){
        if(this.status.equals(TripStatus.UNDECIDED)) throw new IllegalArgumentException("status 가 UNDECIDED 일 경우 startDate와 endDate를 지정해 줄 수 없습니다.");
        Trip trip = Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .title(title)
                .status(this.status)
                .build();
        List<Day> days = createDays(startDate, endDate, trip);

        return Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .title(title)
                .tripPeriod(TripPeriod.of(startDate, endDate))
                .days(days)
                .status(this.status)
                .build();
    }


    private List<Day> createDays(LocalDate startDate, LocalDate endDate, Trip trip){
        List<Day> days = new ArrayList<>();
        LocalDate currendDate = startDate;

        while(!currendDate.isAfter(endDate)){
            days.add(Day.of(currendDate, trip));
            currendDate = currendDate.plusDays(1);
        }

        return days;
    }

}
