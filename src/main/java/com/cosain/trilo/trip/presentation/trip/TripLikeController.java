package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.application.token.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.trip.application.trip.service.trip_like.TripLikeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/likes")
public class TripLikeController {

    private final TripLikeFacade tripLikeFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Login
    public void create(@PathVariable Long tripId, @LoginUser UserPayload userPayload){
        tripLikeFacade.addLike(tripId, userPayload.getId());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Login
    public void delete(@PathVariable Long tripId, @LoginUser UserPayload userPayload){
        tripLikeFacade.removeLike(tripId, userPayload.getId());
    }
}
