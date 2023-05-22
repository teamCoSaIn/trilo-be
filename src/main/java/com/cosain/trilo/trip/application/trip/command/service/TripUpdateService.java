package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.command.service.dto.TripUpdateCommand;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripUpdateService implements TripUpdateUseCase {

    private final TripRepository tripRepository;
    private final DayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void updateTrip(Long tripId, Long tripperId, TripUpdateCommand updateCommand) {
        Trip trip = findTrip(tripId);
        validateTripUpdateAuthority(trip, tripperId);

        trip.changeTitle(updateCommand.getTitle());
        changePeriod(trip, updateCommand.getTripPeriod());
    }

    private Trip findTrip(Long tripId) {
        return tripRepository.findByIdWithDays(tripId)
                .orElseThrow(() -> new TripNotFoundException("일치하는 식별자의 Trip을 찾을 수 없음"));
    }

    private void validateTripUpdateAuthority(Trip trip, Long tripperId) {
        if (!trip.getTripperId().equals(tripperId)) {
            throw new NoTripUpdateAuthorityException("여행을 수정할 권한이 없는 사람이 수정하려고 시도함");
        }
    }

    private void changePeriod(Trip trip, TripPeriod newPeriod) {
        var changePeriodResult = trip.changePeriod(newPeriod);
        List<Day> createdDays = changePeriodResult.getCreatedDays();
        List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();

        if (!createdDays.isEmpty()) {
            dayRepository.saveAll(createdDays);
        }
        if (!deletedDayIds.isEmpty()) {
            scheduleRepository.relocateDaySchedules(trip.getId(), null);
            scheduleRepository.moveSchedulesToTemporaryStorage(trip.getId(), deletedDayIds);
            dayRepository.deleteAllByIds(deletedDayIds);
        }
    }

}
