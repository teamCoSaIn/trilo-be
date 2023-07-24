package com.cosain.trilo.trip.application.trip.service.trip_create;

import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 여행 생성을 수행하는 애플리케이션 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class TripCreateService {

    /**
     * 생성 여행을 저장할 리포지토리
     */
    private final TripRepository tripRepository;

    /**
     * Trip(여행)을 생성하여 등록하고, 생성된 여행의 식별자(id)를 반환합니다.
     * @param command : 여행 생성 명령(비즈니스 입력 모델)
     * @return 생성된 여행의 식별자(id)
     * @see TripCreateCommand
     */
    @Transactional
    public Long createTrip(TripCreateCommand command) {
        Trip trip = Trip.create(command.getTripTitle(), command.getTripperId());
        Trip savedTrip = tripRepository.save(trip);
        return savedTrip.getId();
    }
}
