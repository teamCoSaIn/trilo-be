package com.cosain.trilo.trip.presentation.trip.query;

import com.cosain.trilo.trip.query.application.dto.TemporaryPageResult;
import com.cosain.trilo.trip.query.application.usecase.TemporarySearchUseCase;
import com.cosain.trilo.trip.presentation.trip.query.dto.response.TemporaryPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 여행의 임시보관함 조회
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TripTemporaryStorageQueryController {

    private final TemporarySearchUseCase temporarySearchUseCase;

    @GetMapping("/api/trips/{tripId}/temporary-storage")
    @ResponseStatus(HttpStatus.OK)
    public TemporaryPageResponse findTripTemporaryStorage(@PathVariable Long tripId, Pageable pageable) {
        TemporaryPageResult temporaryPageResult = temporarySearchUseCase.searchTemporary(tripId, pageable);
        return TemporaryPageResponse.from(temporaryPageResult);
    }
}
