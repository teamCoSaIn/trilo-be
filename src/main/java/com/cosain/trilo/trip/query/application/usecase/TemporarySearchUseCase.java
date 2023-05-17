package com.cosain.trilo.trip.query.application.usecase;

import com.cosain.trilo.trip.query.application.dto.TemporaryPageResult;
import org.springframework.data.domain.Pageable;

public interface TemporarySearchUseCase {
    TemporaryPageResult searchTemporary(Long tripId, Pageable pageable);
}
