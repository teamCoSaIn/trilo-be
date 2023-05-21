package com.cosain.trilo.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.trip.query.service.dto.TemporaryPageResult;
import org.springframework.data.domain.Pageable;

public interface TemporarySearchUseCase {
    TemporaryPageResult searchTemporary(Long tripId, Pageable pageable);
}
