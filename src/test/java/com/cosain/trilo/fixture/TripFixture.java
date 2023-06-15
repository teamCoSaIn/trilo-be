package com.cosain.trilo.fixture;

import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.domain.vo.TripTitle;

import java.time.LocalDate;
import java.util.List;

public enum TripFixture {

    UNDECIDED_TRIP(TripStatus.UNDECIDED),
    DECIDED_TRIP(TripStatus.DECIDED),
    ;

    private final TripStatus status;
    TripFixture(TripStatus status){
        this.status = status;
    }

    public Trip createUndecided(Long id, Long tripperId, String rawTitle){
        if(this.status.equals(TripStatus.DECIDED)) throw new IllegalArgumentException("status 가 DECIDED 일 경우 startDate와 endDate를 지정해주어야 합니다.");
        return Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of(rawTitle))
                .status(this.status)
                .tripPeriod(TripPeriod.empty())
                .build();
    }

    public Trip createDecided(Long id, Long tripperId, String rawTitle, LocalDate startDate, LocalDate endDate) {
        if (this.status.equals(TripStatus.UNDECIDED))
            throw new IllegalArgumentException("status 가 UNDECIDED 일 경우 startDate와 endDate를 지정해 줄 수 없습니다.");

        TripPeriod tripPeriod = TripPeriod.of(startDate, endDate);

        Trip trip = Trip.builder()
                .id(id)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of(rawTitle))
                .tripPeriod(tripPeriod)
                .status(this.status)
                .build();

        List<Day> days = createDays(trip, tripPeriod);
        trip.getDays().addAll(days);

        return trip;
    }

    private List<Day> createDays(Trip trip, TripPeriod tripPeriod) {
        return tripPeriod.dateStream()
                .map(date -> this.createDay(date, trip))
                .toList();
    }

    private Day createDay(LocalDate date, Trip trip) {
        DayColor dummyDayColor = DayColor.BLACK;

        return Day.builder()
                .trip(trip)
                .tripDate(date)
                .dayColor(dummyDayColor)
                .build();
    }

}
