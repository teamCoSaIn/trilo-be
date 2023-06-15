package com.cosain.trilo.trip.application.day.command.service;

import com.cosain.trilo.trip.application.day.command.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.command.usecase.DayColorUpdateUseCase;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DayColorUpdateService implements DayColorUpdateUseCase {

    private final DayRepository dayRepository;

    @Override
    public void updateDayColor(Long dayId, Long tripperId, DayColorUpdateCommand command) {
    }
}
