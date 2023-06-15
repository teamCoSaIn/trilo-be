package com.cosain.trilo.trip.application.day.command.usecase;

import com.cosain.trilo.trip.application.day.command.dto.DayColorUpdateCommand;

public interface DayColorUpdateUseCase {

    void updateDayColor(Long dayId, Long tripperId, DayColorUpdateCommand command);
}
