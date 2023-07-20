package com.cosain.trilo.trip.application.event;

import com.cosain.trilo.trip.application.trip.service.trip_all_delete.TripAllDeleteService;
import com.cosain.trilo.user.application.event.UserDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripEventListener {

    private final TripAllDeleteService tripAllDeleteService;

    @EventListener
    public void handle(UserDeleteEvent event){
        Long tripperId = event.getUserId();
        tripAllDeleteService.deleteAllByTripperId(tripperId);
    }
}
