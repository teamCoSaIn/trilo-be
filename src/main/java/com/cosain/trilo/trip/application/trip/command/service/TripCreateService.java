package com.cosain.trilo.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.service.dto.TripCreateCommand;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripCreateService implements TripCreateUseCase {

    private final TripRepository tripRepository;

    /**
     * Trip을 생성하여 등록하고, 식별자를 반환합니다.
     * @param tripperId : 여행자 식별자
     * @param createDto : 여행 생성에 필요한 정보들
     * @return 생성된 여행의 식별자
     */
    @Override
    @Transactional
    public Long createTrip(Long tripperId, TripCreateCommand createDto) {
        Trip trip = Trip.create(createDto.getTitle(), tripperId);
        Trip savedTrip = tripRepository.save(trip);
        return savedTrip.getId();
    }
}
