package com.cosain.trilo.trip.application.trip.service.trip_title_update;

import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TripTitleUpdateService {

    private final TripRepository tripRepository;

    @Transactional
    public void updateTripTitle(TripTitleUpdateCommand command) {
        Trip trip = findTrip(command.getTripId());
        validateTripUpdateAuthority(trip, command.getRequestTripperId());

        trip.changeTitle(command.getTripTitle());
    }

    private Trip findTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("일치하는 식별자의 Trip을 찾을 수 없음"));
    }

    private void validateTripUpdateAuthority(Trip trip, Long tripperId) {
        if (!trip.getTripperId().equals(tripperId)) {
            throw new NoTripUpdateAuthorityException("여행을 수정할 권한이 없는 사람이 수정하려고 시도함");
        }
    }

}
