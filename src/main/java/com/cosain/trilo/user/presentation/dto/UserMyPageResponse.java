package com.cosain.trilo.user.presentation.dto;

import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.user.domain.User;
import lombok.Getter;

@Getter
public class UserMyPageResponse {
    private String name;
    private String imageURL;
    private TripStatistics tripStatistics;

    private UserMyPageResponse(User user, TripStatistics tripStatistics) {
        this.name = user.getName();
        this.imageURL = user.getMyPageImage().getBaseURL().concat(user.getMyPageImage().getFilaName());
        this.tripStatistics = tripStatistics;
    }

    public static UserMyPageResponse of(User user, TripStatistics tripStatistics){
        return new UserMyPageResponse(user, tripStatistics);
    }
}
