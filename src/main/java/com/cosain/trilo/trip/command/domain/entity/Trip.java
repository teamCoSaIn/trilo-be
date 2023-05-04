package com.cosain.trilo.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.dto.ChangeTripPeriodResult;
import com.cosain.trilo.trip.command.domain.exception.EmptyPeriodUpdateException;
import com.cosain.trilo.trip.command.domain.vo.Place;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Entity
@Slf4j
@Table(name = "trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;

    @Column(name = "tripper_id")
    private Long tripperId;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status")
    private TripStatus status;

    @Embedded
    private TripPeriod tripPeriod;

    @OneToMany(mappedBy = "trip")
    private final List<Day> days = new ArrayList<>();

    @OneToMany(mappedBy = "trip")
    @Where(clause = "day_id is NULL")
    @OrderBy("scheduleIndex.value asc")
    private final List<Schedule> temporaryStorage = new ArrayList<>();

    /**
     * 여행(Trip)을 최초로 생성합니다. 최초 생성된 Trip은 UNDECIDED 상태입니다.
     *
     * @param title:    여행의 제목
     * @param tripperId : 여행자의 식별자
     * @return 생성된 Trip
     */
    public static Trip create(String title, Long tripperId) {
        return Trip.builder()
                .tripperId(tripperId)
                .title(title)
                .status(TripStatus.UNDECIDED)
                .tripPeriod(TripPeriod.empty())
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Trip(Long id, Long tripperId, String title, TripStatus status, TripPeriod tripPeriod, List<Day> days, List<Schedule> temporaryStorage) {
        this.id = id;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status;
        this.tripPeriod = tripPeriod;

        if (days != null) {
            this.days.addAll(days);
        }

        if (temporaryStorage != null) {
            this.temporaryStorage.addAll(temporaryStorage);
        }
    }

    /**
     * Trip의 제목을 변경합니다.
     *
     * @param newTitle : 변경할 제목
     */
    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * 기간을 변경합니다. 이에 따라 삭제되는 날짜와, 생성되는 날짜들을 각각 리스트로 반환합니다.
     *
     * @param newPeriod
     * @return ChangeTripPeriodResult
     */
    public ChangeTripPeriodResult changePeriod(TripPeriod newPeriod) {
        TripPeriod oldPeriod = this.tripPeriod;

        // 기존이랑 기간이 같으면 변경 안 하고 반환
        if (oldPeriod.equals(newPeriod)) {
            return ChangeTripPeriodResult.of(Collections.emptyList(), Collections.emptyList());
        }

        // 이미 기간이 정해졌는데, 빈 기간으로 변경하려 할 때
        if (status.equals(TripStatus.DECIDED) && newPeriod.equals(TripPeriod.empty())) {
            throw new EmptyPeriodUpdateException("여행기간이 정해진 상태에서 빈 기간으로 변경하려고 시도함");
        }

        // 여기서부터 기간 실제 변경 발생
        if (status == TripStatus.UNDECIDED) {
            status = TripStatus.DECIDED;
        }
        this.tripPeriod = newPeriod;

        List<Day> deletedDays = deleteUnnecessaryDays(oldPeriod, newPeriod);
        List<Day> createdDays = addNewDays(oldPeriod, newPeriod);
        return ChangeTripPeriodResult.of(deletedDays, createdDays);
    }

    private List<Day> deleteUnnecessaryDays(TripPeriod oldPeriod, TripPeriod newPeriod) {
        TripPeriod overlappedPeriod = oldPeriod.intersection(newPeriod);
        List<Day> deleteDays = days.stream()
                .filter(day -> !day.isIn(overlappedPeriod))
                .toList();
        this.days.removeAll(deleteDays);
        return deleteDays;
    }

    private List<Day> addNewDays(TripPeriod oldPeriod, TripPeriod newPeriod) {
        List<Day> newDays = newPeriod.dateStream()
                .filter(date -> !oldPeriod.contains(date))
                .map(date -> Day.of(date, this))
                .toList();
        this.days.addAll(newDays);
        return newDays;
    }

    public Schedule createSchedule(Day day, String title, Place place) {
        return Schedule.create(day, this, title, place, ScheduleIndex.of(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)%1_000_000_000));
    }
}
