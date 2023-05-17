package com.cosain.trilo.trip.query.presentation.trip.dto;

import com.cosain.trilo.trip.query.application.dto.ScheduleResult;
import com.cosain.trilo.trip.query.application.dto.TemporaryPageResult;
import com.cosain.trilo.trip.query.presentation.schedule.dto.ScheduleDetailResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TemporaryPageResponse {
    private boolean hasNext;
    private List<ScheduleDetailResponse> tempSchedules;

    public static TemporaryPageResponse from(TemporaryPageResult temporaryPageResult){
        List<ScheduleResult> scheduleResults = temporaryPageResult.getScheduleResults();
        List<ScheduleDetailResponse> scheduleDetailResponses = scheduleResults.stream()
                .map(ScheduleDetailResponse::from)
                .collect(Collectors.toList());
        return new TemporaryPageResponse(temporaryPageResult.isHasNext() ,scheduleDetailResponses);
    }

    private TemporaryPageResponse(boolean hasNext, List<ScheduleDetailResponse> tempSchedules){
        this.hasNext = hasNext;
        this.tempSchedules = tempSchedules;
    }
}
