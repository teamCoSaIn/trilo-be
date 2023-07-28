package com.cosain.trilo.trip.infra.repository.jpa;

import com.cosain.trilo.trip.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Trip 엔티티를 조회해오는 Spring Data JPA Repository
 */
public interface JpaTripRepository extends JpaRepository<Trip, Long> {

    /**
     * 인자로 전달받은 식별자의 여행(Day들 포함)을 담은 Optional 을 조회하여 반환받습니다.
     * @param tripId 조회할 여행의 식별자(id)
     * @return 조회해온 여행(Day들 포함)을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    @Query("SELECT t" +
            " FROM Trip as t LEFT JOIN FETCH t.days" +
            " WHERE t.id = :id")
    Optional<Trip> findByIdWithDays(@Param("id") Long tripId);

    @Query("SELECT t FROM Trip as t WHERE t.tripperId = :tripperId")
    List<Trip> findAllByTripperId(@Param("tripperId") Long tripperId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Trip t WHERE t.tripperId = :tripperId")
    void deleteAllByTripperId(@Param("tripperId") Long tripperId);

}
