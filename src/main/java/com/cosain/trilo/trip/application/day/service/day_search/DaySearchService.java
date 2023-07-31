package com.cosain.trilo.trip.application.day.service.day_search;

import com.cosain.trilo.common.exception.day.DayNotFoundException;
import com.cosain.trilo.trip.application.dao.DayQueryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DaySearchService{

    private final DayQueryDAO dayQueryDAO;

    /**
     * Day 및 Day에 속한 일정들에 대한 정보를 조회해옵니다.
     * @param dayId : day의 식별자
     */
    public DayScheduleDetail searchDaySchedule(Long dayId) {
        return findDayWithScheduleByDayId(dayId);
    }

    private DayScheduleDetail findDayWithScheduleByDayId(Long dayId){
        return dayQueryDAO.findDayWithSchedulesByDayId(dayId)
                .orElseThrow(() -> new DayNotFoundException("일치하는 식별자의 여행을 조회할 수 없습니다."));
    }

    /**
     * Trip에 속한 Day 및 그 Day 각각이 각진 일정들에 대한 정보를 목록으로 조회해옵니다.
     * @param tripId : trip의 식별자
     */
    public List<DayScheduleDetail> searchDaySchedules(Long tripId){
        return dayQueryDAO.findDayScheduleListByTripId(tripId);
    }
}
