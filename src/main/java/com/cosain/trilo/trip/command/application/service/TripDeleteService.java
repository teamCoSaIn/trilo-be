package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.exception.NoTripDeleteAuthorityException;
import com.cosain.trilo.trip.command.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.command.application.usecase.TripDeleteUseCase;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TripDeleteService implements TripDeleteUseCase {

    private final TripRepository tripRepository;
    private final DayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void deleteTrip(Long tripId, Long tripperId) {
        Trip trip = findTrip(tripId);
        validateTripDeleteAuthority(trip, tripperId);

        scheduleRepository.deleteAllByTripId(tripId);
        dayRepository.deleteAllByTripId(tripId);
        tripRepository.delete(trip);
    }

    private Trip findTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("삭제하려는 여행이 존재하지 않음"));
    }

    private void validateTripDeleteAuthority(Trip trip, Long tripperId) {
        if (!trip.getTripperId().equals(tripperId)) {
            throw new NoTripDeleteAuthorityException("여행을 삭제할 권한이 없는 사용자가 여행을 삭제하려 시도 함");
        }
    }
}
