package com.cosain.trilo.trip.domain.entity;

import com.cosain.trilo.trip.domain.vo.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 일정의 도메인 엔티티(Entity)입니다.
 */
@Slf4j
@Getter
@ToString(of = {"id", "scheduleTitle", "scheduleContent", "scheduleTime", "place", "scheduleIndex"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules")
@Entity
public class Schedule {

    /**
     * 일정의 식별자(id)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    /**
     * 일정이 속한 Day (null이면 임시보관함)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id")
    private Day day;

    /**
     * 일정이 속한 여행
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    /**
     * 일정의 제목
     * @see ScheduleTitle
     */
    @Embedded
    private ScheduleTitle scheduleTitle;

    /**
     * 일정의 본문
     * @see ScheduleContent
     */
    @Embedded
    private ScheduleContent scheduleContent;

    /**
     * 일정의 시간
     * @see ScheduleTime
     */
    @Embedded
    private ScheduleTime scheduleTime;

    /**
     * 일정이 진행되는 장소
     * @see Place
     */
    @Embedded
    private Place place;

    /**
     * 일정의 ScheduleIndex
     * @see ScheduleIndex
     */
    @Embedded
    private ScheduleIndex scheduleIndex;

    /**
     * 일정을 생성합니다.
     * <p>이 객체는 {@link Trip#createSchedule} 메서드를 통해 생성되어야 하므로 디폴트 접근제어자가 걸려있습니다.</p>
     * <p>명시적으로 전달된 파라미터 외의 필드들은 다음과 같이 초기화됩니다.</p>
     * <ul>
     *     <li>id : null (리포지토리에 저장 후 발급받아서 초기화해야합니다.)</li>
     *     <li>scheduleContent : {@link ScheduleContent#defaultContent()}</li>
     *     <li>scheduleTime : {@link ScheduleTime#defaultTime()}</li>
     * </ul>
     * @param day 일정이 속한 Day(null 일 경우 임시보관함)
     * @param trip 일정이 속한 여행
     * @param scheduleTitle 일정 제목
     * @param place 장소
     * @param scheduleIndex 일정의 {@link ScheduleIndex}
     * @return 일정
     * @see Trip#createSchedule
     */
    static Schedule create(Day day, Trip trip, ScheduleTitle scheduleTitle, Place place, ScheduleIndex scheduleIndex) {
        return Schedule.builder()
                .day(day)
                .trip(trip)
                .scheduleTitle(scheduleTitle)
                .scheduleContent(ScheduleContent.defaultContent())
                .place(place)
                .scheduleIndex(scheduleIndex)
                .scheduleTime(ScheduleTime.defaultTime())
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Schedule(Long id, Day day, Trip trip, ScheduleTitle scheduleTitle, ScheduleContent scheduleContent, ScheduleTime scheduleTime, Place place, ScheduleIndex scheduleIndex) {
        this.id = id;
        this.day = day;
        this.trip = trip;
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent == null ? ScheduleContent.defaultContent() : scheduleContent;
        this.scheduleTime = scheduleTime == null ? ScheduleTime.defaultTime() : scheduleTime;
        this.place = place;
        this.scheduleIndex = scheduleIndex;
    }

    public void changeTitle(ScheduleTitle scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public void changeContent(ScheduleContent scheduleContent) {
        this.scheduleContent = scheduleContent;
    }

    public void changeTime(ScheduleTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    /**
     * 일정의 day와 ScheduleIndex를 변경합니다.
     * 기존의 day 또는 임시보관함이 있으면 해당 Day와 관계를 끊고 새로운 day 또는 임시보관함과 관계를 맺습니다.
     *
     * @param targetDay 도착지점의 Day
     * @param newScheduleIndex 새로 부여받을 ScheduleIndex
     */
    void changePosition(Day targetDay, ScheduleIndex newScheduleIndex) {
        // 기존 Day 또는 임시보관함으로부터 일정 자기 자신을 분리합니다.
        detachSelfFromDayOrTemporaryStorage();

        this.day = targetDay;
        this.scheduleIndex = newScheduleIndex;

        // 새로 속하게된 Day 또는 임시보관함에 일정 자기 자신을 추가합니다.
        attachSelfToDayOrTemporaryStorage();
    }

    /**
     * 자신이 속했던 Day 또는, 임시보관함에서 Schedule 자신을 끊어냅니다.
     */
    private void detachSelfFromDayOrTemporaryStorage() {
        if (day == null) {
            trip.detachScheduleFromTemporaryStorage(this);
            return;
        }
        day.detachSchedule(this);
    }

    /**
     *  새로 속하게된 Day 또는 임시보관함에 자신을 추가합니다.
     */
    private void attachSelfToDayOrTemporaryStorage() {
        if (day == null) {
            trip.attachScheduleToTemporaryStorage(this);
            return;
        }
        day.attachSchedule(this);
    }
}
