package com.cosain.trilo.trip.infra.repository;

import com.cosain.trilo.trip.domain.entity.Like;
import com.cosain.trilo.trip.domain.repository.LikeRepository;
import com.cosain.trilo.trip.infra.repository.jpa.JpaLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final JpaLikeRepository jpaLikeRepository;

    @Override
    public Like save(Like like) {
        return jpaLikeRepository.save(like);
    }

    @Override
    public void delete(Like like) {
        jpaLikeRepository.delete(like);
    }

    @Override
    public boolean existsByTripIdAndTripperId(Long tripId, Long tripperId) {
        return jpaLikeRepository.existsByTripIdAndTripperId(tripId, tripperId);
    }

    @Override
    public Optional<Like> findByTripIdAndTripperId(Long tripId, Long tripperId) {
        return jpaLikeRepository.findByTripperIdAndTripId(tripId, tripperId);
    }
}
