package com.cosain.trilo.trip.presentation.trip.query.dto.response;

import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.presentation.schedule.query.dto.response.ScheduleDetailResponse;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TemporaryPageResponse {
    private boolean hasNext;
    private List<ScheduleDetailResponse> tempSchedules;

    public static TemporaryPageResponse from(Slice<ScheduleDetail> scheduleDetails){
        List<ScheduleDetailResponse> scheduleDetailResponses = scheduleDetails.stream()
                .map(ScheduleDetailResponse::from)
                .collect(Collectors.toList());
        return new TemporaryPageResponse(scheduleDetails.hasNext(), scheduleDetailResponses);
    }

    private TemporaryPageResponse(boolean hasNext, List<ScheduleDetailResponse> tempSchedules){
        this.hasNext = hasNext;
        this.tempSchedules = tempSchedules;
    }
}
