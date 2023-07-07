package com.cosain.trilo.user.presentation.dto;

import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.cosain.trilo.user.domain.User;
import lombok.Getter;

@Getter
public class UserMyPageResponse {
    private String name;
    private String imageURL;
    private TripStatistics tripStatistics;

    private UserMyPageResponse(User user, String imageBaseURL, TripStatistics tripStatistics) {
        this.name = user.getName();
        this.imageURL = imageBaseURL.concat(user.getMyPageImage().getFileName());
        this.tripStatistics = tripStatistics;
    }

    public static UserMyPageResponse of(User user, String imageBaseURL, TripStatistics tripStatistics){
        return new UserMyPageResponse(user, imageBaseURL, tripStatistics);
    }
}
