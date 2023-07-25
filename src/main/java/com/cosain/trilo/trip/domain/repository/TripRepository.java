package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Trip;
import org.springframework.data.repository.query.Param;

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

    Optional<Trip> findById(Long tripId);

    Optional<Trip> findByIdWithDays(@Param("id") Long tripId);

    List<Trip> findAllByTripperId(@Param("tripperId") Long tripperId);

    void delete(Trip trip);

    void deleteAllByTripperId(@Param("tripperId") Long tripperId);
}
