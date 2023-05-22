package com.cosain.trilo.trip.infra.repository.schedule.jpa;

import com.cosain.trilo.trip.infra.dto.QScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import static com.cosain.trilo.trip.domain.entity.QSchedule.schedule;

@RequiredArgsConstructor
public class ScheduleQueryJpaRepositoryImpl implements ScheduleQueryJpaRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public Optional<ScheduleDetail> findScheduleDetailById(Long scheduleId) {
        return Optional.ofNullable(query.select(
            new QScheduleDetail(schedule.id, schedule.day.id, schedule.title, schedule.place.placeName, schedule.place.coordinate.latitude, schedule.place.coordinate.longitude, schedule.scheduleIndex.value, schedule.content))
                .from(schedule)
                .where(schedule.id.eq(scheduleId))
                .fetchOne());
    }

    @Override
    public Slice<ScheduleDetail> findTemporaryScheduleListByTripId(Long tripId, Pageable pageable) {
        JPAQuery<ScheduleDetail> jpaQuery = query.select(new QScheduleDetail(schedule.id, schedule.day.id, schedule.title, schedule.place.placeName, schedule.place.coordinate.latitude, schedule.place.coordinate.longitude, schedule.scheduleIndex.value, schedule.content))
                .from(schedule)
                .where(schedule.trip.id.eq(tripId).and(schedule.day.id.isNull()))
                .orderBy(schedule.scheduleIndex.value.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        long totalCount = query.from(schedule)
                .where(schedule.trip.id.eq(tripId).and(schedule.day.id.isNull()))
                .fetchCount();

        return new PageImpl<>(jpaQuery.fetch(), pageable, totalCount);
    }
}
