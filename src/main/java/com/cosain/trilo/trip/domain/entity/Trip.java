package com.cosain.trilo.trip.domain.entity;

import com.cosain.trilo.trip.domain.dto.ChangeTripPeriodResult;
import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.exception.EmptyPeriodUpdateException;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.trip.domain.exception.InvalidTripDayException;
import com.cosain.trilo.trip.domain.exception.MidScheduleIndexConflictException;
import com.cosain.trilo.trip.domain.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

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
    private TripTitle tripTitle;

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
     * @param tripTitle:    여행의 제목
     * @param tripperId : 여행자의 식별자
     * @return 생성된 Trip
     */
    public static Trip create(TripTitle tripTitle, Long tripperId) {
        return Trip.builder()
                .tripperId(tripperId)
                .tripTitle(tripTitle)
                .status(TripStatus.UNDECIDED)
                .tripPeriod(TripPeriod.empty())
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Trip(Long id, Long tripperId, TripTitle tripTitle, TripStatus status, TripPeriod tripPeriod, List<Day> days, List<Schedule> temporaryStorage) {
        this.id = id;
        this.tripperId = tripperId;
        this.tripTitle = tripTitle;
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
    public void changeTitle(TripTitle newTitle) {
        this.tripTitle = newTitle;
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
        validateTripDayRelationShip(day);
        return (day == null)
                ? makeTemporaryStorageSchedule(title, place)
                : day.createSchedule(title, place);
    }

    /**
     * Trip에 Day가 속해있는 지 검증합니다.
     * @param day
     */
    private void validateTripDayRelationShip(Day day) {
        if (day == null) {
            return;
        }
        if (!day.isBelongTo(this)) {
            throw new InvalidTripDayException("해당 Day는 Trip의 Day가 아님");
        }
    }

    private Schedule makeTemporaryStorageSchedule(String title, Place place) {
        Schedule schedule = Schedule.create(null, this, title, place, generateNextTemporaryStorageScheduleIndex());
        temporaryStorage.add(schedule);
        return schedule;
    }

    private ScheduleIndex generateNextTemporaryStorageScheduleIndex() {
        return (temporaryStorage.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : temporaryStorage.get(temporaryStorage.size() - 1).getScheduleIndex().generateNextIndex();
    }

    /**
     * Schedule을 지정한 Day의 지정한 순서로 이동합니다. 이때, 지정한 Day가 null이면 임시보관함으로 이동합니다. 같은 Day를 지정하면
     * 같은 곳 안에서 순서 변경이 일어납니다.
     * @param schedule
     * @param targetDay
     * @param targetOrder
     */
    public ScheduleMoveDto moveSchedule(Schedule schedule, Day targetDay, int targetOrder) {
        validateTripDayRelationShip(targetDay);
        return (targetDay == null)
                ? moveScheduleToTemporaryStorage(schedule, targetOrder)
                : targetDay.moveSchedule(schedule, targetOrder);
    }

    /**
     * Schedule을 임시보관함의 지정 순서로 이동시킵니다.
     *
     * @param schedule
     * @param targetOrder
     */
    private ScheduleMoveDto moveScheduleToTemporaryStorage(Schedule schedule, int targetOrder) {
        if (targetOrder < 0 || targetOrder > this.temporaryStorage.size()) {
            throw new InvalidScheduleMoveTargetOrderException("일정을 지정 위치로 옮기려 시도했으나, 유효한 순서 범위를 벗어남");
        }
        Day beforeDay = schedule.getDay();
        if (isSamePositionMove(schedule, targetOrder)) {
            return ScheduleMoveDto.ofNotPositionChanged(schedule.getId(), beforeDay);
        }
        if (targetOrder == this.temporaryStorage.size()) {
            moveScheduleToTemporaryStorageTail(schedule);
            return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, null);
        }
        if (targetOrder == 0) {
            moveScheduleToTemporaryStorageHead(schedule);
            return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, null);
        }
        moveScheduleToTemporaryStorageMiddle(schedule, targetOrder);
        return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, null);
    }

    /**
     * 스케쥴을 옮길 때 대상이 되는 순서로 옮길 경우, 기존과 상대적 순서가 똑같은 지 여부를 확인합니다. 예를 들어 임시보관함 1번 위치에 있던 일정을
     * 1번 위치에 옮기거나, 2번 위치로 옮기는 경우는 결국 기존과 상대적 순서가 같습니다.
     * @param schedule
     * @param targetOrder
     * @return
     */
    private boolean isSamePositionMove(Schedule schedule, int targetOrder) {
        return schedule.getDay() == null && (targetOrder == temporaryStorage.indexOf(schedule) || targetOrder == temporaryStorage.indexOf(schedule) + 1);
    }

    /**
     * 지정 Schedule을 임시보관함의 제일 앞에 둡니다.
     * @param schedule
     */
    private void moveScheduleToTemporaryStorageHead(Schedule schedule) {
        ScheduleIndex newScheduleIndex = generateTemporaryStorageHeadIndex();

        schedule.changePosition(null, newScheduleIndex);
        this.temporaryStorage.add(schedule);
    }

    /**
     * 지정 Schedule을 임시보관함의 제일 뒤에 둡니다.
     * @param schedule
     */
    private void moveScheduleToTemporaryStorageTail(Schedule schedule) {
        ScheduleIndex newScheduleIndex = generateNextTemporaryStorageScheduleIndex();

        schedule.changePosition(null, newScheduleIndex);
        this.temporaryStorage.add(schedule);
    }

    /**
     * 지정 Schedule을 임시보관함의 지정 순서에 중간삽입합니다. 그 순서에 있던 일정은 뒤로 밀려납니다.
     * @param schedule
     * @param targetOrder
     */
    private void moveScheduleToTemporaryStorageMiddle(Schedule schedule, int targetOrder) {
        ScheduleIndex destinationOrderScheduleIndex = temporaryStorage.get(targetOrder).getScheduleIndex();
        ScheduleIndex previousOrderScheduleIndex = temporaryStorage.get(targetOrder - 1).getScheduleIndex();

        ScheduleIndex newScheduleIndex = destinationOrderScheduleIndex.mid(previousOrderScheduleIndex);

        if (newScheduleIndex.equals(destinationOrderScheduleIndex) || newScheduleIndex.equals(previousOrderScheduleIndex)) {
            throw new MidScheduleIndexConflictException("중간 삽입 인덱스 충돌 발생 -> 인덱스 재정렬 필요");
        }

        schedule.changePosition(null, newScheduleIndex);
        this.temporaryStorage.add(schedule);
    }

    /**
     * 임시보관함의 제일 앞 인덱스보다 한 단계 더 앞선 인덱스를 만듭니다.
     * @return
     */
    private ScheduleIndex generateTemporaryStorageHeadIndex() {
        return (temporaryStorage.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : temporaryStorage.get(0).getScheduleIndex().generateBeforeIndex();
    }

    /**
     * 지정 Schedule을 임시보관함 컬렉션에서 분리합니다.
     * @param schedule
     */
    void detachScheduleFromTemporaryStorage(Schedule schedule) {
        this.temporaryStorage.remove(schedule);
    }
}
