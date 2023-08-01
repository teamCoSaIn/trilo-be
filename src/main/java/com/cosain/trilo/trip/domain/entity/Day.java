package com.cosain.trilo.trip.domain.entity;

import com.cosain.trilo.common.exception.schedule.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.common.exception.schedule.MidScheduleIndexConflictException;
import com.cosain.trilo.common.exception.schedule.ScheduleIndexRangeException;
import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.vo.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 여행 기간의 특정 하루를 나타내는 도메인 엔티티입니다.
 */
@Getter
@ToString(of = {"id", "tripDate"})
@Entity
@Table(name = "days")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Day {

    /**
     * Day가 가질 수 있는 일정의 최대 갯수
     */
    public static final int MAX_DAY_SCHEDULE_COUNT = 10;

    /**
     * Day의 식별자(id)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long id;

    /**
     * Day의 날짜
     */
    @Column(name = "trip_date")
    private LocalDate tripDate;

    /**
     * Day가 속한 여행(Trip)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    /**
     * Day의 색상
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "day_color")
    private DayColor dayColor;

    /**
     * <p>Day에 소속된 일정({@link Schedule})들의 컬렉션입니다.
     * <p>일정들은 {@link ScheduleIndex} 기준 오름차순으로 정렬되어 있습니다.</p>
     * @see Schedule
     * @see ScheduleIndex
     */
    @OneToMany(mappedBy = "day")
    @OrderBy("scheduleIndex.value asc")
    private final List<Schedule> schedules = new ArrayList<>();

    static Day of(LocalDate tripDate, Trip trip, Random random){
        return Day.builder()
                .tripDate(tripDate)
                .trip(trip)
                .dayColor(DayColor.random(random))
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Day(Long id, LocalDate tripDate, Trip trip, DayColor dayColor, List<Schedule> schedules) {
        this.id = id;
        this.tripDate = tripDate;
        this.trip = trip;
        this.dayColor = dayColor;
        if (schedules != null) {
            this.schedules.addAll(schedules);
        }
    }

    /**
     * Day의 색상을 변경합니다.
     * @param dayColor : 변경할 색상
     */
    public void changeColor(DayColor dayColor) {
        this.dayColor = dayColor;
    }

    /**
     * Day가 지정 TripPeriod에 속하는 지 여부를 반환합니다.
     * @param tripPeriod : 기간
     * @return 소속 여부
     */
    public boolean isIn(TripPeriod tripPeriod) {
        return tripPeriod.contains(tripDate);
    }

    Schedule createSchedule(ScheduleTitle scheduleTitle, Place place) {
        Schedule schedule = Schedule.create(this, trip, scheduleTitle, place, generateSchedulesTailIndex());
        schedules.add(schedule);
        return schedule;
    }

    /**
     * Day의 제일 맨 뒤 ScheduleIndex보다 한 단계 더 뒤의 ScheduleIndex를 만듭니다.
     * @return 새로운 맨 뒤 ScheduleIndex
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    private ScheduleIndex generateSchedulesTailIndex() {
        return (schedules.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : schedules.get(schedules.size() - 1).getScheduleIndex().generateNextIndex();
    }

    /**
     * Day의 제일 맨 앞 ScheduleIndex보다 한 단계 더 앞의 ScheduleIndex를 만듭니다.
     * @return 새로운 맨 뒤 ScheduleIndex
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    private ScheduleIndex generateSchedulesHeadIndex() {
        return (schedules.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : schedules.get(0).getScheduleIndex().generateBeforeIndex();
    }

    /**
     * 일정을 Day의 지정 순서로 옮깁니다.
     * @param schedule 옮길 일정
     * @param targetOrder Day에서 몇 번째 순서로 옮길 지
     * @throws InvalidScheduleMoveTargetOrderException 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     * @throws MidScheduleIndexConflictException 중간 삽입 과정에서 충돌이 발생했을 때(정상흐름 변경 가능)
     */
    ScheduleMoveDto moveSchedule(Schedule schedule, int targetOrder)
            throws InvalidScheduleMoveTargetOrderException, ScheduleIndexRangeException, MidScheduleIndexConflictException {
        // 일단 앞에서 Schedule이 Trip과 관련된 Schedule이라는 것은 검증 됨

        if (targetOrder < 0 || targetOrder > this.schedules.size()) {
            // Day 내에서, 0번째 순서 아래 이전으로 이동시키려 하거나, 제일 큰 순서보다 큰 순서로 이동하면 예외 발생
            throw new InvalidScheduleMoveTargetOrderException("일정을 지정 위치로 옮기려 시도했으나, 유효한 순서 범위를 벗어남");
        }

        Day beforeDay = schedule.getDay(); // 옮기기 이전 소속한 Day

        if (isSamePositionMove(schedule, targetOrder)) {
            // 같은 위치에서 이동
            return ScheduleMoveDto.ofNotPositionChanged(schedule.getId(), beforeDay);
        }
        if (targetOrder == this.schedules.size()) {
            // 끝으로 이동
            moveScheduleToTail(schedule);
            return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, this);
        }
        if (targetOrder == 0) {
            // 맨 앞 이동
            moveScheduleToHead(schedule);
            return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, this);
        }
        // 중간 삽입
        moveScheduleToMiddle(schedule, targetOrder);
        return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, this);
    }

    /**
     * 스케쥴을 옮길 때 대상이 되는 순서로 옮길 경우, 기존과 상대적 순서가 똑같은 지 여부를 확인합니다. 예를 들어 Day의 Schedules 상에서 1번 위치에 있던 일정을
     * 1번 위치에 옮기거나, 2번 위치로 옮기는 경우는 결국 기존과 상대적 순서가 같습니다.
     * @param schedule 옮길 일정
     * @param targetOrder 대상 순서
     * @return 기존과 같은 위치이면 true, 다른 위치면 false
     */
    private boolean isSamePositionMove(Schedule schedule, int targetOrder) {
        return (schedule.getDay() != null)
                && Objects.equals(this.id, schedule.getDay().getId())
                && (targetOrder == schedules.indexOf(schedule) || targetOrder == schedules.indexOf(schedule) + 1);
    }

    /**
     * 일정을 Schedules의 맨 앞으로 옮깁니다.
     * @param schedule 옮길 일정
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     */
    private void moveScheduleToHead(Schedule schedule) throws ScheduleIndexRangeException {
        ScheduleIndex newScheduleIndex = generateSchedulesHeadIndex();

        schedule.changePosition(this, newScheduleIndex);
    }

    /**
     * 일정을 Schedules의 맨 뒤로 옮깁니다.
     * @param schedule 옮길 일정
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     */
    private void moveScheduleToTail(Schedule schedule) throws ScheduleIndexRangeException {
        ScheduleIndex newScheduleIndex = generateSchedulesTailIndex();
        schedule.changePosition(this, newScheduleIndex);
    }

    /**
     * 지정 Schedule을 지정한 순서에 놓음
     * @param schedule 옮길 일정
     * @param targetOrder 대상 순서
     * @throws MidScheduleIndexConflictException 중간 삽입 과정에서 충돌이 발생했을 때(정상흐름 변경 가능)
     */
    private void moveScheduleToMiddle(Schedule schedule, int targetOrder) throws MidScheduleIndexConflictException {
        // 도착지의 대상 순서에 위치하는 일정과 그 앞에 위치한 일정의 순서값을 얻어옴
        ScheduleIndex targetOrderScheduleIndex = schedules.get(targetOrder).getScheduleIndex();
        ScheduleIndex previousOrderScheduleIndex = schedules.get(targetOrder - 1).getScheduleIndex();

        // 중간 삽입 될 위치의 ScheduleIndex를 계산하여 생성
        ScheduleIndex newScheduleIndex = targetOrderScheduleIndex.mid(previousOrderScheduleIndex);

        // 중간 삽입 시 기존 순서값들과 충돌이 발생하면 예외 발생 -> 외부 계층에서 잡아서 처리해야함
        if (newScheduleIndex.equals(targetOrderScheduleIndex) || newScheduleIndex.equals(previousOrderScheduleIndex)) {
            throw new MidScheduleIndexConflictException("중간 삽입 인덱스 충돌 발생 -> 인덱스 재정렬 필요");
        }

        // 일정을 이동
        schedule.changePosition(this, newScheduleIndex);
    }

    /**
     * 자기 자신이 가진 Schedules에서, 지정 Schedule을 분리함
     * @param schedule : 끊어낼 Schedule
     */
    void detachSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
    }

    /**
     * Day가 지정 Trip에 속해 있는 지 여부를 반환
     * @param trip 여행
     * @return 여행에 속해 있으면 true, 속해있지 않으면 false
     */
    boolean isBelongTo(Trip trip) {
        return this.trip.equals(trip);
    }

    /**
     * 일정을 컬렉션에 추가합니다.
     * @param schedule 일정
     */
    void attachSchedule(Schedule schedule) {
        this.schedules.add(schedule);
    }
}
