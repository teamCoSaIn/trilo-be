package com.cosain.trilo.trip.application.trip.command.usecase;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripTitleUpdateCommand;

public interface TripTitleUpdateUseCase {

    /**
     * Trip의 제목을 Update 합니다.
     * @param tripId : 변경할 Trip의 식별자
     * @param tripperId : 변경을 시도하는 사용자의 식별자
     * @param command : 변경 Command
     */
    void updateTripTitle(Long tripId, Long tripperId, TripTitleUpdateCommand command);
}
