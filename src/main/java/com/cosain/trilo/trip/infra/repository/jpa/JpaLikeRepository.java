package com.cosain.trilo.trip.infra.repository.jpa;

import com.cosain.trilo.trip.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaLikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByTripperIdAndTripId(Long tripId, Long tripperId);

    boolean existsByTripIdAndTripperId(Long tripId, Long tripperId);
}
