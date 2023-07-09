package com.cosain.trilo.trip.application.trip.service;

import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.dto.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPeriodUpdateService {

    private final TripRepository tripRepository;
    private final DayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void updateTripPeriod(Long tripId, Long tripperId, TripPeriodUpdateCommand updateCommand) {
        Trip trip = findTrip(tripId);
        validateTripUpdateAuthority(trip, tripperId);
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
