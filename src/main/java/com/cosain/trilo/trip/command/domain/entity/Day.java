package com.cosain.trilo.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.vo.Place;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "day")
    @OrderBy("scheduleIndex.value asc")
    private List<Schedule> schedules = new ArrayList<>();

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

    /**
     * Day가 지정 TripPeriod에 속하는 지 여부를 반환합니다.
     * @param tripPeriod : 기간
     * @return 소속 여부
     */
    public boolean isIn(TripPeriod tripPeriod) {
        return tripPeriod.contains(tripDate);
    }

    Schedule createSchedule(String title, Place place) {
        Schedule schedule = Schedule.create(this, trip, title, place, generateNextScheduleIndex());
        schedules.add(schedule);
        return schedule;
    }

    private ScheduleIndex generateNextScheduleIndex() {
        return (schedules.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : schedules.get(schedules.size() - 1).getScheduleIndex().generateNextIndex();
    }
}
