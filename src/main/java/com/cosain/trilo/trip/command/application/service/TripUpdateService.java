package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.TripUpdateCommand;
import com.cosain.trilo.trip.command.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.command.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.command.application.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.command.domain.dto.ChangeTripPeriodResult;
import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripUpdateService implements TripUpdateUseCase {

    private final TripRepository tripRepository;
    private final DayRepository dayRepository;

    @Override
    @Transactional
    public void updateTrip(Long tripId, Long tripperId, TripUpdateCommand updateCommand) {
        Trip trip = findTrip(tripId);
        validateTripUpdateAuthority(trip, tripperId);

        trip.changeTitle(updateCommand.getTitle());
        ChangeTripPeriodResult result = trip.updatePeriod(updateCommand.getTripPeriod());

        deleteUnnecessaryDays(result.getDeletedDays());
        saveCreatedDays(result.getCreatedDays());
    }

    private void validateTripUpdateAuthority(Trip trip, Long tripperId) {
        if (!trip.getTripperId().equals(tripperId)) {
            throw new NoTripUpdateAuthorityException("여행을 수정할 권한이 없는 사람이 수정하려고 시도함");
        }
    }

    private Trip findTrip(Long tripId) {
        return tripRepository.findByIdWithDays(tripId)
                .orElseThrow(() -> new TripNotFoundException("일치하는 식별자의 Trip을 찾을 수 없음"));
    }

    private void deleteUnnecessaryDays(List<Day> deletedDays) {
        if (deletedDays.isEmpty()) {
            return;
        }
        dayRepository.deleteDays(deletedDays);
    }

    private void saveCreatedDays(List<Day> createdDays) {
        if (createdDays.isEmpty()) {
            return;
        }
        dayRepository.saveAll(createdDays);
    }
}
