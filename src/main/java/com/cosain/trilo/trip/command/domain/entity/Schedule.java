package com.cosain.trilo.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.vo.Place;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(of = {"id", "title", "content", "place"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules")
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = true)
    private Day day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Embedded
    private Place place;

    public static Schedule create(Day day, Trip trip, String title, Place place) {
        return Schedule.builder()
                .day(day)
                .trip(trip)
                .title(title)
                .place(place)
                .build();
    }

    @Builder(access = AccessLevel.PUBLIC)
    private Schedule(Long id, Day day, Trip trip, String title, String content, Place place) {
        this.id = id;
        this.day = day;
        this.trip = trip;
        this.title = title;
        this.content = content;
        this.place = place;
    }

    public void changeTitle(String title){
        this.title = title;
    }
    public void changeContent(String content){
        this.content = content;
    }
}
