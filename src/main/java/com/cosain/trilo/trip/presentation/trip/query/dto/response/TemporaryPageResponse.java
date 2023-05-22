package com.cosain.trilo.trip.presentation.trip.query.dto.response;

import com.cosain.trilo.trip.application.schedule.query.usecase.dto.ScheduleResult;
import com.cosain.trilo.trip.application.trip.query.usecase.dto.TemporaryPageResult;
import com.cosain.trilo.trip.presentation.schedule.query.dto.response.ScheduleDetailResponse;
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
