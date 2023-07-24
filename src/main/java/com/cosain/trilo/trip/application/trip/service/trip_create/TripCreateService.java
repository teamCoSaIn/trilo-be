package com.cosain.trilo.trip.application.trip.service.trip_create;

import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripCreateService {

    private final TripRepository tripRepository;

    /**
     * Trip을 생성하여 등록하고, 식별자를 반환합니다.
     * @param createCommand : 여행 생성에 필요한 정보들
     * @return 생성된 여행의 식별자
     */
    @Transactional
    public Long createTrip(TripCreateCommand createCommand) {
        Trip trip = Trip.create(createCommand.getTripTitle(), createCommand.getTripperId());
        Trip savedTrip = tripRepository.save(trip);
        return savedTrip.getId();
    }
}
