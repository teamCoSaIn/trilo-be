package com.cosain.trilo.trip.application.day.service.day_color_update;

import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoDayUpdateAuthorityException;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DayColorUpdateService {

    private final DayRepository dayRepository;

    @Transactional
    public void updateDayColor(DayColorUpdateCommand command) {
        Day day = findDay(command.getDayId());

        validateDayUpdateAuthority(day, command.getRequestTripperId());
        day.changeColor(command.getDayColor());
    }

    private Day findDay(Long dayId) {
        return dayRepository.findByIdWithTrip(dayId)
                .orElseThrow(() -> new DayNotFoundException("일치하는 식별자의 day를 찾지 못 함"));
    }

    private void validateDayUpdateAuthority(Day day, Long requestTripperId) {
        Long realDayOwnerId = day.getTrip().getTripperId();

        if (!requestTripperId.equals(realDayOwnerId)) {
            throw new NoDayUpdateAuthorityException("Day를 수정할 권한이 없는 사람이 수정하려 시도함");
        }
    }

}
