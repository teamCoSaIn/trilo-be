package com.cosain.trilo.trip.infra.dao.querydsl;

import com.cosain.trilo.trip.application.day.service.day_search.DayScheduleDetail;
import com.cosain.trilo.trip.application.day.service.day_search.QDayScheduleDetail;
import com.cosain.trilo.trip.application.day.service.day_search.QScheduleSummary;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.cosain.trilo.trip.domain.entity.QDay.day;
import static com.cosain.trilo.trip.domain.entity.QSchedule.schedule;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Component
@RequiredArgsConstructor
public class QuerydslDayQueryRepository {

    private final JPAQueryFactory query;

    public Optional<DayScheduleDetail> findDayWithSchedulesByDayId(Long dayId) {
        DayScheduleDetail dayScheduleDetail = query
                .from()
                .from(day)
                .innerJoin(day.schedules, schedule)
                .where(day.id.eq(dayId))
                .orderBy(schedule.scheduleIndex.value.asc())
                .transform(groupBy(day.id).as(new QDayScheduleDetail(
                        day.id,
                        day.trip.id,
                        day.tripDate,
                        day.dayColor,
                        GroupBy.list(new QScheduleSummary(
                                schedule.id,
                                schedule.scheduleTitle.value,
                                schedule.place.placeName,
                                schedule.place.placeId,
                                schedule.place.coordinate.latitude,
                                schedule.place.coordinate.longitude
                        ))
                ))).get(dayId);

        return Optional.ofNullable(dayScheduleDetail);
    }

    public List<DayScheduleDetail> findDayScheduleListByTripId(Long tripId){
        return query.from(day)
                .leftJoin(day.schedules, schedule)
                .where(day.trip.id.eq(tripId))
                .orderBy(day.tripDate.asc(), schedule.scheduleIndex.value.asc())
                .transform(
                        groupBy(day.id).list(new QDayScheduleDetail(
                                day.id,
                                day.trip.id,
                                day.tripDate,
                                day.dayColor,
                                list(new QScheduleSummary(
                                        schedule.id,
                                        schedule.scheduleTitle.value,
                                        schedule.place.placeName,
                                        schedule.place.placeId,
                                        schedule.place.coordinate.latitude,
                                        schedule.place.coordinate.longitude
                                ).skipNulls())
                        ))
                );
    }
}
