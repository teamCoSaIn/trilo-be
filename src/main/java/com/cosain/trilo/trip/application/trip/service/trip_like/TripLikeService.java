package com.cosain.trilo.trip.application.trip.service.trip_like;

import com.cosain.trilo.common.exception.trip.LikeNotFoundException;
import com.cosain.trilo.common.exception.trip.TripAlreadyLikedException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.domain.entity.Like;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.LikeRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TripLikeService {

    private final TripRepository tripRepository;
    private final LikeRepository likeRepository;

    public void addLike(Long tripId, Long tripperId){
        validateNotLiked(tripId, tripperId);
        Trip trip = findTrip(tripId);
        likeRepository.save(Like.of(tripId, tripperId));
        trip.increaseLikeCount();
    }

    private void validateNotLiked(Long tripId, Long tripperId){
        if(likeRepository.existsByTripIdAndTripperId(tripId, tripperId)){
            throw new TripAlreadyLikedException();
        }
    }

    public void removeLike(Long tripId, Long tripperId){
        Trip trip = findTrip(tripId);
        Like likeRelation = findLikeRelation(tripId, tripperId);
        likeRepository.delete(likeRelation);
        trip.decreaseLikeCount();
    }

    private Trip findTrip(Long tripId){
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }

    private Like findLikeRelation(Long tripId, Long tripperId){
        return likeRepository.findByTripIdAndTripperId(tripId, tripperId)
                .orElseThrow(LikeNotFoundException::new);
    }
}
