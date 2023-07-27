package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Trip;

import java.util.List;
import java.util.Optional;

/**
 * 여행 엔티티 또는 여행 엔티티에 관한 정보를 조회해오거나 등록/수정/삭제하는 리포지토리입니다.
 */
public interface TripRepository {

    /**
     * 여행을 저장소에 등록 후, 저장된 여행을 반환받습니다.
     * @param trip : 저장할 여행
     * @return : 저장된 여행
     */
    Trip save(Trip trip);

    /**
     * 인자로 전달받은 식별자의 여행을 담은 Optional 을 조회하여 반환받습니다.
     * @param tripId 조회할 여행의 식별자(id)
     * @return 조회해온 여행을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    Optional<Trip> findById(Long tripId);

    /**
     * 인자로 전달받은 식별자의 여행(Day들 포함)을 담은 Optional 을 조회하여 반환받습니다.
     * @param tripId 조회할 여행의 식별자(id)
     * @return 조회해온 여행(Day들 포함)을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    Optional<Trip> findByIdWithDays(Long tripId);

    List<Trip> findAllByTripperId(Long tripperId);

    /**
     * 인자로 전달받은 여행을 삭제합니다.
     * @param trip 삭제할 여행
     */
    void delete(Trip trip);

    void deleteAllByTripperId(Long tripperId);
}
