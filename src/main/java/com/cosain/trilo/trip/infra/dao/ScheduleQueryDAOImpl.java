package com.cosain.trilo.trip.infra.dao;

import com.cosain.trilo.trip.application.dao.ScheduleQueryDAO;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListQueryParam;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListSearchResult;
import com.cosain.trilo.trip.infra.dao.querydsl.QuerydslScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduleQueryDAOImpl implements ScheduleQueryDAO {

    private final QuerydslScheduleQueryRepository querydslScheduleQueryRepository;

    @Override
    public Optional<ScheduleDetail> findScheduleDetailById(Long scheduleId) {
        return querydslScheduleQueryRepository.findScheduleDetailById(scheduleId);
    }

    @Override
    public TempScheduleListSearchResult findTemporarySchedules(TempScheduleListQueryParam queryParam) {
        return querydslScheduleQueryRepository.findTemporarySchedules(queryParam);
    }

    @Override
    public boolean existById(Long scheduleId) {
        return querydslScheduleQueryRepository.existById(scheduleId);
    }
}
