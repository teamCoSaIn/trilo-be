package com.cosain.trilo.trip.presentation.trip.dto.response;

import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class TemporaryPageResponse {
    private boolean hasNext;
    private List<ScheduleSummary> tempSchedules;

    public static TemporaryPageResponse from(Slice<ScheduleSummary> scheduleSummaries){
        return new TemporaryPageResponse(scheduleSummaries.hasNext(), scheduleSummaries.getContent());
    }

    private TemporaryPageResponse(boolean hasNext, List<ScheduleSummary> tempSchedules){
        this.hasNext = hasNext;
        this.tempSchedules = tempSchedules;
    }
}
