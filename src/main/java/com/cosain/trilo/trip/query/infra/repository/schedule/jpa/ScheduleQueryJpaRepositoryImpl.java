package com.cosain.trilo.trip.query.infra.repository.schedule.jpa;

import com.cosain.trilo.trip.command.domain.entity.QSchedule;
import com.cosain.trilo.trip.query.infra.dto.QScheduleDetail;
import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.cosain.trilo.trip.command.domain.entity.QSchedule.schedule;

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
}
