package com.cosain.trilo.trip.query.infra.repository.day.jpa;

import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.query.infra.dto.QDayScheduleDetail;
import com.cosain.trilo.trip.query.infra.dto.QDayScheduleDetail_ScheduleSummary;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.cosain.trilo.trip.command.domain.entity.QDay.day;
import static com.cosain.trilo.trip.command.domain.entity.QSchedule.schedule;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@RequiredArgsConstructor
public class DayQueryJpaRepositoryImpl implements DayQueryJpaRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId) {

        DayScheduleDetail dayScheduleDetail = query
                .from(day)
                .innerJoin(day.schedules, schedule)
                .where(day.id.eq(dayId))
                .orderBy(schedule.scheduleIndex.value.asc())
                .transform(groupBy(day.id).as(new QDayScheduleDetail(
                        day.id,
                        day.trip.id,
                        day.tripDate,
                        list(new QDayScheduleDetail_ScheduleSummary(
                                schedule.id,
                                schedule.title,
                                schedule.place.placeName,
                                schedule.place.coordinate.latitude,
                                schedule.place.coordinate.longitude
                        ))
                ))).get(dayId);

        return Optional.ofNullable(dayScheduleDetail);
    }
}
