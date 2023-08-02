package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Like;

import java.util.Optional;

public interface LikeRepository {
    Like save(Like like);

    void delete(Like like);

    boolean existsByTripIdAndTripperId(Long tripId, Long tripperId);

    Optional<Like> findByTripIdAndTripperId(Long tripId, Long tripperId);
}
