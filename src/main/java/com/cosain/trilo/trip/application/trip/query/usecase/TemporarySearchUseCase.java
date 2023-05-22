package com.cosain.trilo.trip.application.trip.query.usecase;

import com.cosain.trilo.trip.application.trip.query.usecase.dto.TemporaryPageResult;
import org.springframework.data.domain.Pageable;

public interface TemporarySearchUseCase {
    TemporaryPageResult searchTemporary(Long tripId, Pageable pageable);
}
