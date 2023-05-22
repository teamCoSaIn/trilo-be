package com.cosain.trilo.trip.application.trip.command.usecase;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;

public interface TripUpdateUseCase {

    /**
     * Trip을 Update 합니다.
     * @param tripId : 변경할 Trip의 식별자
     * @param tripperId : 변경을 시도하는 사용자의 식별자
     * @param updateCommand : 변경 Command
     */
    void updateTrip(Long tripId, Long tripperId, TripUpdateCommand updateCommand);
}
