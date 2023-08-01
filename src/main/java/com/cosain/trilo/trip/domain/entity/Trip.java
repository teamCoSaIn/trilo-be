package com.cosain.trilo.trip.domain.entity;

import com.cosain.trilo.common.exception.day.InvalidTripDayException;
import com.cosain.trilo.common.exception.schedule.InvalidScheduleMoveTargetOrderException;
import com.cosain.trilo.common.exception.schedule.MidScheduleIndexConflictException;
import com.cosain.trilo.common.exception.schedule.ScheduleIndexRangeException;
import com.cosain.trilo.common.exception.trip.EmptyPeriodUpdateException;
import com.cosain.trilo.trip.domain.dto.ChangeTripPeriodResult;
import com.cosain.trilo.trip.domain.dto.ScheduleMoveDto;
import com.cosain.trilo.trip.domain.vo.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 여행의 도메인 엔티티(Entity)입니다.
 */
@Getter
@Slf4j
@ToString(of = {"id", "tripperId", "tripTitle", "status", "tripPeriod", "tripImage"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trip")
@Entity
public class Trip {

    /**
     * 하나의 여행이 가질 수 있는 일정의 최대 갯수
     */
    public static final int MAX_TRIP_SCHEDULE_COUNT = 110;

    /**
     * 여행의 식별자(id)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;

    /**
     * 여행을 소유한 여행자(사용자)의 식별자(id)
     */
    @Column(name = "tripper_id")
    private Long tripperId;

    /**
     * 여행의 제목
     *
     * @see TripTitle
     */
    @Embedded
    private TripTitle tripTitle;

    /**
     * 여행의 상태
     *
     * @see TripStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status")
    private TripStatus status;

    /**
     * 여행의 기간
     *
     * @see TripPeriod
     */
    @Embedded
    private TripPeriod tripPeriod;

    /**
     * 여행에 소속된 Day들의 컬렉션
     *
     * @see Day
     */
    @OneToMany(mappedBy = "trip")
    private final List<Day> days = new ArrayList<>();

    /**
     * 여행의 이미지
     *
     * @see TripImage
     */
    @Embedded
    private TripImage tripImage;

    /**
     * <p>여행의 임시보관함에 소속된 일정({@link Schedule})들의 컬렉션입니다. 어떤 {@link Day}에도 속해있지 않은 일정들이 여기에 보관됩니다.</p>
     * <p>일정들은 {@link ScheduleIndex} 기준 오름차순으로 정렬되어 있습니다.</p>
     *
     * @see Schedule
     * @see ScheduleIndex
     */
    @OneToMany(mappedBy = "trip")
    @Where(clause = "day_id is NULL")
    @OrderBy("scheduleIndex.value asc")
    private final List<Schedule> temporaryStorage = new ArrayList<>();

    /**
     * <p>여행(Trip)을 최초로 생성합니다.</p>
     * <p>명시적으로 전달된 파라미터 외의 필드들은 다음과 같이 초기화됩니다.</p>
     * <ul>
     *     <li>id : null (리포지토리에 저장 후 발급받아서 초기화해야합니다.)</li>
     *     <li>status : {@link TripStatus#UNDECIDED}</li>
     *     <li>period : {@link TripPeriod#empty()}</li>
     *     <li>image : {@link TripImage#defaultImage()}</li>
     * </ul>
     *
     * @param tripTitle: 여행의 제목
     * @param tripperId  : 여행자의 식별자
     * @return 생성된 여행(Trip)
     * @see TripStatus
     * @see TripPeriod
     * @see TripImage
     */
    public static Trip create(TripTitle tripTitle, Long tripperId) {
        return Trip.builder()
                .tripperId(tripperId)
                .tripTitle(tripTitle)
                .status(TripStatus.UNDECIDED)
                .tripImage(TripImage.defaultImage())
                .tripPeriod(TripPeriod.empty())
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Trip(Long id, Long tripperId, TripTitle tripTitle, TripStatus status, TripPeriod tripPeriod, TripImage tripImage, List<Day> days, List<Schedule> temporaryStorage) {
        this.id = id;
        this.tripperId = tripperId;
        this.tripTitle = tripTitle;
        this.status = status;
        this.tripPeriod = tripPeriod;
        this.tripImage = tripImage == null ? TripImage.defaultImage() : tripImage;
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

    public void changeImage(TripImage tripImage) {
        this.tripImage = tripImage;
    }

    /**
     * 기간을 변경합니다. 여행 기간 수정 결과를 반환합니다.
     *
     * @param newPeriod 새로운 기간
     * @return 여행기간 수정 결과(생성되는 기간의 Day들, 삭제해야할 Day들 ...)
     * @throws EmptyPeriodUpdateException 기간이 정해져 있는데 빈 기간으로 수정하려 할 때
     */
    public ChangeTripPeriodResult changePeriod(TripPeriod newPeriod) throws EmptyPeriodUpdateException {
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

        List<Day> deletedDays = deleteUnnecessaryDays(oldPeriod, newPeriod); // 삭제된 Day들
        List<Day> createdDays = addNewDays(oldPeriod, newPeriod); // 새로 생성된 Day들
        return ChangeTripPeriodResult.of(deletedDays, createdDays);
    }

    /**
     * 새로운 기간에 속하지 않는 Day들을 컬렉션에서 제거한뒤, 이들을 모아서 반환합니다.
     *
     * @param oldPeriod 기존의 기간
     * @param newPeriod 새로운 기간
     * @return 새로운 기간에 속하지 않는 Day들
     */
    private List<Day> deleteUnnecessaryDays(TripPeriod oldPeriod, TripPeriod newPeriod) {
        TripPeriod overlappedPeriod = oldPeriod.intersection(newPeriod); // 겹치는 기간 구하기
        List<Day> deleteDays = days.stream()
                .filter(day -> !day.isIn(overlappedPeriod)) // 겹치는 기간에 속하지 않는 날짜들만 수집 -> 삭제 대상
                .toList();
        this.days.removeAll(deleteDays); // 삭제 대상을 컬렉션에서 제거
        return deleteDays;
    }

    /**
     * 새로운 여행 기간 중, 기존 기간에 속하지 않은 날짜들에 해당하는 Day들을 만듭니다.
     *
     * @param oldPeriod 기존 기간
     * @param newPeriod 새로운 기간
     * @return 기간 변경으로 인해 새로 생성되는 Day들
     */
    private List<Day> addNewDays(TripPeriod oldPeriod, TripPeriod newPeriod) {
        Random random = new Random(); // Day 색상값을 랜덤으로 지정하여 생성하기 위한 random

        List<Day> newDays = newPeriod.dateStream()
                .filter(date -> !oldPeriod.contains(date)) // 기존 기간에 속하지 않는 날짜들을 구함
                .map(date -> Day.of(date, this, random)) // 날짜 정보를 기반으로 Day 생성
                .toList();
        this.days.addAll(newDays); // 새로운 Day들을 컬렉션에 추가
        return newDays;
    }

    /**
     * <p>일정을 생성합니다.</p>
     * <p>임시보관함에 생성시 맨 앞에, Day에 생성시 맨 뒤에 생성됩니다.</p>
     * @param day           일정을이 생성될 Day(null 일 경우 임시보관함)
     * @param scheduleTitle 일정의 제목
     * @param place         장소
     * @return 일정
     * @throws InvalidTripDayException     Day가 이 Trip의 여행이 아닐 때
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     */
    public Schedule createSchedule(Day day, ScheduleTitle scheduleTitle, Place place) throws InvalidTripDayException, ScheduleIndexRangeException {
        // 여행의 Day 인지 검증
        validateTripDayRelationShip(day);

        return (day == null)
                ? makeTemporaryStorageScheduleToHead(scheduleTitle, place) // 일정을 임시보관함 맨 앞에 생성
                : day.createScheduleToTail(scheduleTitle, place); // Day의 맨 뒤에 생성
    }

    /**
     * Trip에 Day가 속해있는 지 검증합니다.
     *
     * @param day Trip에 속해있는지 판별하고 싶은 Day
     * @throws InvalidTripDayException Day가 이 Trip의 여행이 아닐 때
     */
    private void validateTripDayRelationShip(Day day) throws InvalidTripDayException {
        if (day == null) {
            return;
        }
        if (!day.isBelongTo(this)) {
            throw new InvalidTripDayException("해당 Day는 Trip의 Day가 아님");
        }
    }

    /**
     * 일정을 임시보관함의 맨 뒤에 생성합니다.
     * @param scheduleTitle 일정의 제목
     * @param place 장소
     * @return 일정
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     */
    private Schedule makeTemporaryStorageScheduleToHead(ScheduleTitle scheduleTitle, Place place) throws ScheduleIndexRangeException {
        // 일정 생성(맨 앞 ScheduleIndex 가짐)
        Schedule schedule = Schedule.create(null, this, scheduleTitle, place, generateTemporaryStorageHeadIndex());

        // 임시보관함 컬렉션 맨 앞에 추가
        temporaryStorage.add(0, schedule);
        return schedule;
    }

    /**
     * <p>일정을 지정한 Day(null 일 경우 임시보관함)의 지정한 순서로 이동합니다.</p>
     * <p>지정한 Day가 null이면 임시보관함으로 이동합니다.</p>
     *
     * @param schedule    옮길 일정
     * @param targetDay   도착지 Day (null 일 경우 임시보관함)
     * @param targetOrder 해당 Day 또는 임시보관함에서 몇 번째로 옮길 지
     * @throws InvalidTripDayException                 Day가 이 Trip의 여행이 아닐 때
     * @throws InvalidScheduleMoveTargetOrderException 요청한 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     * @throws ScheduleIndexRangeException             새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     * @throws MidScheduleIndexConflictException       중간 삽입 과정에서 충돌이 발생했을 때(정상흐름 변경 가능)
     */
    public ScheduleMoveDto moveSchedule(Schedule schedule, Day targetDay, int targetOrder)
            throws InvalidTripDayException, InvalidScheduleMoveTargetOrderException, ScheduleIndexRangeException, MidScheduleIndexConflictException {

        // Day가 이 여행의 Day인지 검증 -> 여행의 Day가 아니면 예외 발생
        validateTripDayRelationShip(targetDay);

        return (targetDay == null)
                ? moveScheduleToTemporaryStorage(schedule, targetOrder) // day가 null이면 임시보관함으로 일정 이동
                : targetDay.moveSchedule(schedule, targetOrder); // day가 null 이 아니면 해당 day로 일정 이동 (Day에게 위임)
    }

    /**
     * 일정을 임시보관함의 지정 순서로 이동시킵니다.
     *
     * @param schedule    이동시킬 일정
     * @param targetOrder 임시보관함에서 몇 번째로 옮길 지
     * @throws InvalidScheduleMoveTargetOrderException 대상 순서가 0보다 작거나, 허용하는 순서보다 큰 경우
     * @throws ScheduleIndexRangeException             새로 생성되는 ScheduleIndex가 범위를 벗어날 때(정상흐름 변경 가능)
     * @throws MidScheduleIndexConflictException       중간 삽입 과정에서 충돌이 발생했을 때(정상흐름 변경 가능)
     */
    private ScheduleMoveDto moveScheduleToTemporaryStorage(Schedule schedule, int targetOrder)
            throws InvalidScheduleMoveTargetOrderException, ScheduleIndexRangeException, MidScheduleIndexConflictException {

        if (targetOrder < 0 || targetOrder > this.temporaryStorage.size()) {
            // 임시보관함 내에서, 0번째 순서 아래 이전으로 이동시키려 하거나, 제일 큰 순서보다 큰 순서로 이동하면 예외 발생
            throw new InvalidScheduleMoveTargetOrderException("일정을 지정 위치로 옮기려 시도했으나, 유효한 순서 범위를 벗어남");
        }

        Day beforeDay = schedule.getDay(); // 옮기기 이전 소속한 Day

        if (isSamePositionMove(schedule, targetOrder)) {
            // 같은 위치에서 이동
            return ScheduleMoveDto.ofNotPositionChanged(schedule.getId(), beforeDay);
        }
        if (targetOrder == this.temporaryStorage.size()) {
            // 끝으로 이동
            moveScheduleToTemporaryStorageTail(schedule);
            return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, null);
        }
        if (targetOrder == 0) {
            // 맨 앞 이동
            moveScheduleToTemporaryStorageHead(schedule);
            return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, null);
        }
        // 중간 삽입
        moveScheduleToTemporaryStorageMiddle(schedule, targetOrder);
        return ScheduleMoveDto.ofPositionChanged(schedule.getId(), beforeDay, null);
    }

    /**
     * <p>스케쥴을 옮길 때 대상이 되는 순서로 옮길 경우, 기존과 상대적 순서가 똑같은 지 여부를 확인합니다.</p>
     * <p>예를 들어 임시보관함 1번 위치에 있던 일정을, 1번 위치에 옮기거나, 2번 위치로 옮기는 경우는 결국 기존과 상대적 순서가 같습니다.</p>
     *
     * @param schedule    옮기고자 하는 일정
     * @param targetOrder 임시보관함에서 몇 번째로 옮길 지
     * @return 결과적으로 같은 위치로 이동하면 true, 아니면 false
     */
    private boolean isSamePositionMove(Schedule schedule, int targetOrder) {
        return schedule.getDay() == null && (targetOrder == temporaryStorage.indexOf(schedule) || targetOrder == temporaryStorage.indexOf(schedule) + 1);
    }

    /**
     * 지정 일정을 임시보관함의 제일 앞에 둡니다.
     *
     * @param schedule 옮길 일정
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    private void moveScheduleToTemporaryStorageHead(Schedule schedule) throws ScheduleIndexRangeException {
        // 맨 앞에 둘 때 부여할 ScheduleIndex을 생성합니다.
        ScheduleIndex newScheduleIndex = generateTemporaryStorageHeadIndex();

        // 일정을 임시보관함으로 이동합니다.
        schedule.changePosition(null, newScheduleIndex);
    }

    /**
     * 지정 Schedule을 임시보관함의 제일 뒤에 둡니다.
     *
     * @param schedule 옮길 일정
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    private void moveScheduleToTemporaryStorageTail(Schedule schedule) throws ScheduleIndexRangeException {
        // 맨 뒤에 둘 때 부여할 ScheduleIndex을 생성합니다.
        ScheduleIndex newScheduleIndex = generateTemporaryStorageTailIndex();

        // 일정을 이동합니다.
        schedule.changePosition(null, newScheduleIndex);
    }

    /**
     * 지정 Schedule을 임시보관함의 지정 순서에 중간삽입합니다. 그 순서에 있던 일정은 뒤로 밀려납니다.
     *
     * @param schedule    옮길 일정
     * @param targetOrder 대상 순서
     * @throws MidScheduleIndexConflictException 중간삽입 과정에서 ScheduleIndex 값 충돌이 발생했을 때
     */
    private void moveScheduleToTemporaryStorageMiddle(Schedule schedule, int targetOrder) throws MidScheduleIndexConflictException {
        // 도착지의 대상 순서에 위치하는 일정과 그 앞에 위치한 일정의 순서값을 얻어옴
        ScheduleIndex targetOrderScheduleIndex = temporaryStorage.get(targetOrder).getScheduleIndex();
        ScheduleIndex previousOrderScheduleIndex = temporaryStorage.get(targetOrder - 1).getScheduleIndex();

        // 중간 삽입 될 위치의 ScheduleIndex를 계산하여 생성
        ScheduleIndex newScheduleIndex = targetOrderScheduleIndex.mid(previousOrderScheduleIndex);

        // 중간 삽입 시 기존 순서값들과 충돌이 발생하면 예외 발생 -> 외부 계층에서 잡아서 처리해야함
        if (newScheduleIndex.equals(targetOrderScheduleIndex) || newScheduleIndex.equals(previousOrderScheduleIndex)) {
            throw new MidScheduleIndexConflictException("중간 삽입 인덱스 충돌 발생 -> 인덱스 재정렬 필요");
        }

        // 일정을 이동
        schedule.changePosition(null, newScheduleIndex);
    }

    /**
     * 임시보관함의 제일 앞 인덱스보다 한 단계 더 앞선 인덱스를 만듭니다.
     *
     * @return 새로운 맨 앞 ScheduleIndex
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    private ScheduleIndex generateTemporaryStorageHeadIndex() throws ScheduleIndexRangeException {
        return (temporaryStorage.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : temporaryStorage.get(0).getScheduleIndex().generateBeforeIndex();
    }

    /**
     * 임시보관함의 제일 맨 뒤 인덱스보다 한 단계 더 뒤의 인덱스를 만듭니다.
     *
     * @return 새로운 맨 뒤 ScheduleIndex
     * @throws ScheduleIndexRangeException 새로 생성되는 ScheduleIndex가 범위를 벗어날 때
     */
    private ScheduleIndex generateTemporaryStorageTailIndex() throws ScheduleIndexRangeException {
        return (temporaryStorage.isEmpty())
                ? ScheduleIndex.ZERO_INDEX
                : temporaryStorage.get(temporaryStorage.size() - 1).getScheduleIndex().generateNextIndex();
    }

    /**
     * 지정 일정을 임시보관함 컬렉션에서 분리합니다.
     *
     * @param schedule 분리할 일정
     */
    void detachScheduleFromTemporaryStorage(Schedule schedule) {
        this.temporaryStorage.remove(schedule);
    }

    /**
     * 지정 일정을 임시보관함 컬렉션에 추가합니다.
     *
     * @param schedule 추가할 일정
     */
    void attachScheduleToTemporaryStorage(Schedule schedule) {
        this.temporaryStorage.add(schedule);
    }
}
