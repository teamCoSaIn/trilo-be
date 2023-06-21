package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TempSchedulePageCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TemporarySearchUseCase {
    Slice<ScheduleSummary> searchTemporary(Long tripId, TempSchedulePageCondition tempSchedulePageCondition, Pageable pageable);
}
