package com.cosain.trilo.fixture;

import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.*;

import java.util.Comparator;
import java.util.List;

public class ScheduleFixture {

    /**
     * 임시보관함에 속한 일정을 생성하고, 실제로 해당 Trip의 임시보관함 컬렉션에 일정을 추가합니다.
     * @param scheduleId : 일정에 부여할 식별자(id)
     * @param trip : 여행
     * @param scheduleIndexValue : 순서값(다른 일정들의 순서값과 비교했을 때 작을 수록 앞에 위치하고, 클 수록 뒤에 위치함)
     * @return 일정
     */
    public static Schedule temporaryStorage_Id(Long scheduleId, Trip trip, long scheduleIndexValue) {
        Schedule schedule = temporaryStorage_Id_NoAdd(scheduleId, trip, scheduleIndexValue);
        addToCollection(schedule, trip, null);
        return schedule;
    }

    /**
     * 임시보관함에 속한 일정을 생성하고, 실제로 해당 Trip의 임시보관함 컬렉션에 일정을 추가합니다. 이 때 식별자는 부여하지 않습니다.
     * @param trip : 여행
     * @param scheduleIndexValue : 순서값(다른 일정들의 순서값과 비교했을 때 작을 수록 앞에 위치하고, 클 수록 뒤에 위치함)
     * @return 일정
     */
    public static Schedule temporaryStorage_NullId(Trip trip, long scheduleIndexValue) {
        return temporaryStorage_Id(null, trip, scheduleIndexValue);
    }

    /**
     * 임시보관함에 속한 일정을 생성합니다. 단, 임시보관함 컬렉션에 일정을 추가하지 않습니다.
     * @param scheduleId : 일정에 부여할 식별자(id)
     * @param trip : 여행
     * @param scheduleIndexValue : 순서값(다른 일정들의 순서값과 비교했을 때 작을 수록 앞에 위치하고, 클 수록 뒤에 위치함)
     * @return 일정
     */
    public static Schedule temporaryStorage_Id_NoAdd(Long scheduleId, Trip trip, long scheduleIndexValue) {
        return createMockSchedule(scheduleId, trip, null, scheduleIndexValue, "일정 제목", "일정 본문");
    }

    /**
     * Day에 속한 일정을 생성하고 실제 Day의 Schedule 컬렉션에 추가합니다.
     * @param scheduleId : 일정에 부여할 식별자(id)
     * @param trip : 여행
     * @param day : 속한 Day
     * @param scheduleIndexValue : 순서값(다른 일정들의 순서값과 비교했을 때 작을 수록 앞에 위치하고, 클 수록 뒤에 위치함)
     * @return 일정
     */
    public static Schedule day_Id(Long scheduleId, Trip trip, Day day, long scheduleIndexValue) {
        Schedule schedule = day_Id_notAdd(scheduleId, trip, day, scheduleIndexValue);
        addToCollection(schedule, trip, day);
        return schedule;
    }

    /**
     * Day에 속한 일정을 생성하고 실제 Day의 Schedule 컬렉션에 추가합니다. 이 때 식별자 값을 부여하지 않습니다.
     * @param trip : 여행
     * @param day : 속한 Day
     * @param scheduleIndexValue : 순서값(다른 일정들의 순서값과 비교했을 때 작을 수록 앞에 위치하고, 클 수록 뒤에 위치함)
     * @return 일정
     */
    public static Schedule day_NullId(Trip trip, Day day, long scheduleIndexValue) {
        return day_Id(null, trip, day, scheduleIndexValue);
    }

    /**
     * Day에 속한 일정을 생성하고 실제 Day의 Schedule 컬렉션에 추가합니다. 이 때, 실제 Day의 컬렉션에 추가하지는 않습니다.
     * @param scheduleId : 부여할 일정 식별자(id)
     * @param trip : 여행
     * @param day : 속한 Day
     * @param scheduleIndexValue : 순서값(다른 일정들의 순서값과 비교했을 때 작을 수록 앞에 위치하고, 클 수록 뒤에 위치함)
     * @return 일정
     */
    public static Schedule day_Id_notAdd(Long scheduleId, Trip trip, Day day, long scheduleIndexValue) {
        return createMockSchedule(scheduleId, trip, day, scheduleIndexValue, "일정 제목", "일정 본문");
    }

    private static Schedule createMockSchedule(Long scheduleId, Trip trip, Day day, long scheduleIndexValue, String rawTitle, String rawContent) {
        return Schedule.builder()
                .id(scheduleId)
                .trip(trip)
                .day(day)
                .scheduleIndex(ScheduleIndex.of(scheduleIndexValue))
                .scheduleTitle(ScheduleTitle.of(rawTitle))
                .scheduleContent(ScheduleContent.of(rawContent))
                .scheduleTime(ScheduleTime.defaultTime())
                .place(Place.of("place-id", "place-name", Coordinate.of(37.123, 132.145)))
                .build();
    }

    private static void addToCollection(Schedule schedule, Trip trip, Day day) {
        List<Schedule> schedules = (day == null) ? trip.getTemporaryStorage() : day.getSchedules();
        schedules.add(schedule);
        schedules.sort(Comparator.comparing(sch -> sch.getScheduleIndex().getValue()));
    }

}
