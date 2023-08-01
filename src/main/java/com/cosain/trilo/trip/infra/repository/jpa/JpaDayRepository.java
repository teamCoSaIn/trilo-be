package com.cosain.trilo.trip.infra.repository.jpa;

import com.cosain.trilo.trip.domain.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Day 엔티티를 관리하는 Spring Data JPA Repository
 */
public interface JpaDayRepository extends JpaRepository<Day, Long> {

    /**
     * 인자로 전달받은 식별자의 Day(속한 여행 포함)을 담은 Optional을 조회하여 반환받습니다.
     * @param dayId Day의 id(식별자)
     * @return 조회해온 Day(속한 Day 포함)을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    @Query("SELECT d " +
            "FROM Day as d JOIN FETCH d.trip " +
            "WHERE d.id = :dayId")
    Optional<Day> findByIdWithTrip(@Param("dayId") Long dayId);

    /**
     * 전달받은 Id들에 해당하는 Day들을 모두 삭제합니다.
     * @param dayIds 삭제할 Day의 id들
     * @return 삭제된 Day의 갯수
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Day d WHERE d in :dayIds")
    int deleteAllByIds(@Param("dayIds") List<Long> dayIds);

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 Day 엔티티들을 모두 제거합니다.
     * @param tripId 여행의 식별자(id)
     */
    @Modifying
    @Query("DELETE FROM Day as d WHERE d.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);

    @Modifying
    @Query("DELETE FROM Day as d WHERE d.trip.id in :tripIds")
    void deleteAllByTripIds(@Param("tripIds") List<Long> tripIds);
}
