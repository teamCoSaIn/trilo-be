package com.cosain.trilo.trip.application.trip.command.usecase;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripPeriodUpdateCommand;

public interface TripPeriodUpdateUseCase {

    /**
     * Trip의 기간을 Update 합니다.
     * @param tripId : 변경할 Trip의 식별자
     * @param tripperId : 변경을 시도하는 사용자의 식별자
     * @param updateCommand : 변경 Command
     */
    void updateTripPeriod(Long tripId, Long tripperId, TripPeriodUpdateCommand updateCommand);
}
