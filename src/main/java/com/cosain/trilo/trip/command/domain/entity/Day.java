package com.cosain.trilo.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.exception.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.trip.command.domain.exception.MidScheduleIndexConflictException;
import com.cosain.trilo.trip.command.domain.vo.Place;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import jakarta.persistence.*;
import lombok.*;

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
    private final List<Schedule> schedules = new ArrayList<>();

    /**
     * 비즈니스 코드에서 Day 생성은 Trip 에서만 할 수 있다.
     */
    public static Day of(LocalDate tripDate, Trip trip){
        return Day.builder()
                .tripDate(tripDate)
                .trip(trip)
                .build();
    }

    @Builder(access = AccessLevel.PUBLIC)
    private Day(Long id, LocalDate tripDate, Trip trip, List<Schedule> schedules) {
        this.id = id;
        this.tripDate = tripDate;
        this.trip = trip;
        if (schedules != null) {
            this.schedules.addAll(schedules);
        }
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

    /**
     * Schdeules의 제일 앞 index를 새로 만듭니다.
     * @return 제일 앞 인덱스(기존 일정이 없으면 0번 인덱스, 있으면 제일 앞보다 한단계 낮은 인덱스)
     */
    private ScheduleIndex generateSchedulesHeadIndex() {
        return (schedules.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : schedules.get(0).getScheduleIndex().generateBeforeIndex();
    }

    /**
     * 일정을 지정 위치로 옮깁니다.
     * @param schedule
     * @param targetOrder
     */
    void moveSchedule(Schedule schedule, int targetOrder) {
        // 일단 앞에서 Schedule이 Trip과 관련된 Schedule이라는 것은 검증 됨
        // TODO: 임시보관함과 Day에서 동일한 로직이 중복됨 -> 리팩터링을 해야할 것인가
        if (targetOrder < 0 || targetOrder > this.schedules.size()) {
            throw new InvalidScheduleMoveTargetOrderException("일정을 지정 위치로 옮기려 시도했으나, 유효한 순서 범위를 벗어남");
        }
        if (isSamePositionMove(schedule, targetOrder)) {
            return;
        }
        if (targetOrder == this.schedules.size()) {
            moveScheduleToTail(schedule);
            return;
        }
        if (targetOrder == 0) {
            moveScheduleToHead(schedule);
            return;
        }
        moveScheduleToMiddle(schedule, targetOrder);
    }

    /**
     * 스케쥴을 옮길 때 대상이 되는 순서로 옮길 경우, 기존과 상대적 순서가 똑같은 지 여부를 확인합니다. 예를 들어 Day의 Schedules 상에서 1번 위치에 있던 일정을
     * 1번 위치에 옮기거나, 2번 위치로 옮기는 경우는 결국 기존과 상대적 순서가 같습니다.
     * @param schedule
     * @param targetOrder
     * @return
     */
    private boolean isSamePositionMove(Schedule schedule, int targetOrder) {
        return schedule.getDay().equals(this) && (targetOrder == schedules.indexOf(schedule) || targetOrder == schedules.indexOf(schedule) + 1);
    }

    /**
     * 일정을 Schedules의 맨 앞으로 옮깁니다.
     * @param schedule
     */
    private void moveScheduleToHead(Schedule schedule) {
        ScheduleIndex newScheduleIndex = generateSchedulesHeadIndex();

        schedule.changePosition(this, newScheduleIndex);
        schedules.add(schedule);
    }

    /**
     * 일정을 Schedules의 맨 뒤로 옮깁니다.
     * @param schedule
     */
    private void moveScheduleToTail(Schedule schedule) {
        ScheduleIndex newScheduleIndex = generateNextScheduleIndex();

        schedule.changePosition(this, newScheduleIndex);
        schedules.add(schedule);
    }

    /**
     * 지정 Schedule을 지정한 순서에 놓음
     * @param schedule
     * @param targetOrder
     */
    private void moveScheduleToMiddle(Schedule schedule, int targetOrder) {
        ScheduleIndex destinationOrderScheduleIndex = schedules.get(targetOrder).getScheduleIndex();
        ScheduleIndex previousOrderScheduleIndex = schedules.get(targetOrder - 1).getScheduleIndex();

        ScheduleIndex newScheduleIndex = destinationOrderScheduleIndex.mid(previousOrderScheduleIndex);

        if (newScheduleIndex.equals(destinationOrderScheduleIndex) || newScheduleIndex.equals(previousOrderScheduleIndex)) {
            throw new MidScheduleIndexConflictException("중간 삽입 인덱스 충돌 발생 -> 인덱스 재정렬 필요");
        }

        schedule.changePosition(this, newScheduleIndex);
        schedules.add(schedule);
    }

    /**
     * 자기 자신이 가진 Schedules에서, 지정 Schedule을 분리함
     * @param schedule : 끊어낼 Schedule
     */
    void detachSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
    }
}
