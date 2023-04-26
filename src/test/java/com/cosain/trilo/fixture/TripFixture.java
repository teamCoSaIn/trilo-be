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

    public Trip create(Long id, Long tripperId, String title){
        return Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .title(title)
                .status(this.status)
                .tripPeriod(TripPeriod.empty())
                .build();
    }

    public Trip create(Long id, Long tripperId, String title, LocalDate startDate, LocalDate endDate){
        List<Day> days = createDays(startDate, endDate, this.create(id, tripperId, title));
        return Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .title(title)
                .tripPeriod(TripPeriod.of(startDate, endDate))
                .days(days)
                .status(this.status)
                .build();
    }

    public Trip create(Long tripperId, String title, LocalDate startDate, LocalDate endDate){
        return Trip.builder()
                .tripperId(tripperId)
                .title(title)
                .tripPeriod(TripPeriod.of(startDate, endDate))
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
