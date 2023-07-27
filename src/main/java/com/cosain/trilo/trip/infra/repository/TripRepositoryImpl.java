package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaTripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 여행 엔티티 또는 여행 엔티티에 관한 정보를 조회해오거나 등록/수정/삭제하는 리포지토리 구현체입니다.
 * @see TripRepository
 */
@Component
@RequiredArgsConstructor
public class TripRepositoryImpl implements TripRepository {

    /**
     * Trip 엔티티를 조회해오는 Spring Data JPA Repository
     */
    private final JpaTripRepository jpaTripRepository;

    /**
     * <p>여행을 저장하고, 저장된 여행을 반환합니다. </p>
     * @param trip : 저장할 여행
     * @return 저장된 여행
     */
    @Override
    public Trip save(Trip trip) {
        // JPA에 의해 여행 엔티티가 저장되고 식별자(id)가 초기화된 뒤 동일한 참조의 객체가 그대로 반환됨에 주의
        return jpaTripRepository.save(trip);
    }

    /**
     * 인자로 전달받은 식별자의 여행을 담은 Optional 을 조회하여 반환받습니다.
     * @param tripId 조회할 여행의 식별자(id)
     * @return 조회해온 여행을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    @Override
    public Optional<Trip> findById(Long tripId) {
        return jpaTripRepository.findById(tripId);
    }

    /**
     * 인자로 전달받은 식별자의 여행(Day들 포함)을 담은 Optional 을 조회하여 반환받습니다.
     * @param tripId 조회할 여행의 식별자(id)
     * @return 조회해온 여행(Day들 포함)을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    @Override
    public Optional<Trip> findByIdWithDays(Long tripId) {
        return jpaTripRepository.findByIdWithDays(tripId);
    }

    @Override
    public List<Trip> findAllByTripperId(Long tripperId) {
        return jpaTripRepository.findAllByTripperId(tripperId);
    }

    /**
     * 인자로 전달받은 여행을 삭제합니다.
     * @param trip 삭제할 여행
     */
    @Override
    public void delete(Trip trip) {
        jpaTripRepository.delete(trip);
    }

    @Override
    public void deleteAllByTripperId(Long tripperId) {
        jpaTripRepository.deleteAllByTripperId(tripperId);
    }
}
