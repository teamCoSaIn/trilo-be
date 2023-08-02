package com.cosain.trilo.trip.application.trip.service.trip_like;

import com.cosain.trilo.common.exception.LockNotAcquiredException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TripLikeFacade {

    private final RedissonClient redissonClient;
    private final TripLikeService tripLikeService;

    private void performWithLock(Long tripId, Long tripperId, TripLikeOperation operation){
        RLock lock = redissonClient.getLock(tripId.toString());

        try{
            boolean available = lock.tryLock(1, 3, TimeUnit.SECONDS);

            if(!available){
                throw new LockNotAcquiredException();
            }

            operation.perform(tripId, tripperId);
        }catch (InterruptedException e){
            throw new LockNotAcquiredException();
        }finally {
            lock.unlock();
        }
    }

    public void addLike(Long tripId, Long tripperId){
        performWithLock(tripId, tripperId, tripLikeService::addLike);
    }

    public void removeLike(Long tripId, Long tripperId){
        performWithLock(tripId, tripperId, tripLikeService::removeLike);
    }

}
