package com.cosain.trilo.trip.infra.dao.querydsl;

import com.cosain.trilo.trip.application.day.service.day_search.QScheduleSummary;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.QScheduleDetail;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.cosain.trilo.trip.domain.entity.QSchedule.schedule;

@Component
@RequiredArgsConstructor
public class QuerydslScheduleQueryRepository {

    private final JPAQueryFactory query;

    public Optional<ScheduleDetail> findScheduleDetailById(Long scheduleId) {
        return Optional.ofNullable(query.select(
                        new QScheduleDetail(schedule.id, schedule.day.id, schedule.scheduleTitle.value, schedule.place.placeName, schedule.place.coordinate.latitude, schedule.place.coordinate.longitude, schedule.scheduleIndex.value, schedule.scheduleContent.value, schedule.scheduleTime.startTime, schedule.scheduleTime.endTime))
                .from(schedule)
                .where(schedule.id.eq(scheduleId))
                .fetchOne());
    }

    public Slice<ScheduleSummary> findTemporaryScheduleListByTripId(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable) {

        Long scheduleId = tempSchedulePageCondition.getScheduleId();
        ScheduleIndex scheduleIndex = null;
        if (scheduleId != null) {
            scheduleIndex = query.select(schedule.scheduleIndex)
                    .from(schedule)
                    .where(schedule.id.eq(scheduleId))
                    .fetchOne();
        }

        JPAQuery<ScheduleSummary> jpaQuery = query.select(new QScheduleSummary(schedule.id, schedule.scheduleTitle.value, schedule.place.placeName, schedule.place.placeId, schedule.place.coordinate.latitude, schedule.place.coordinate.longitude))
                .from(schedule)
                .where(schedule.trip.id.eq(tripId),
                        schedule.day.id.isNull(),
                        gtScheduleIndex(scheduleIndex)
                )
                .orderBy(schedule.scheduleIndex.value.asc())
                .limit(pageable.getPageSize() + 1);

        List<ScheduleSummary> results = jpaQuery.fetch();
        boolean hasNext = isHasNext(results, pageable);

        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression gtScheduleIndex(ScheduleIndex scheduleIndex) {
        return scheduleIndex == null
                ? null
                : schedule.scheduleIndex.value.gt(scheduleIndex.getValue());
    }

    private boolean isHasNext(List<?> results, Pageable pageable) {
        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return hasNext;
    }

    public boolean existById(Long scheduleId) {
        Integer fetchOne = query.selectOne()
                .from(schedule)
                .where(schedule.id.eq(scheduleId))
                .fetchFirst();

        return fetchOne != null;
    }
}
