package com.cosain.trilo.trip.infra.repository.day.jpa;

import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import com.cosain.trilo.trip.infra.dto.QDayScheduleDetail;
import com.cosain.trilo.trip.infra.dto.QScheduleSummary;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.cosain.trilo.trip.domain.entity.QDay.day;
import static com.cosain.trilo.trip.domain.entity.QSchedule.schedule;
import static com.querydsl.core.group.GroupBy.groupBy;

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
                        GroupBy.list(new QScheduleSummary(
                                schedule.id,
                                schedule.title,
                                schedule.place.placeName,
                                schedule.place.placeId,
                                schedule.place.coordinate.latitude,
                                schedule.place.coordinate.longitude
                        ))
                ))).get(dayId);

        return Optional.ofNullable(dayScheduleDetail);
    }
}
