package com.cosain.trilo.trip.application.event;

import com.cosain.trilo.trip.application.trip.service.trip_all_delete.TripAllDeleteService;
import com.cosain.trilo.user.application.event.UserDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TripEventListener {

    private final TripAllDeleteService tripAllDeleteService;

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener
    public void handle(UserDeleteEvent event){
        Long tripperId = event.getUserId();
        tripAllDeleteService.deleteAllByTripperId(tripperId);
    }
}
