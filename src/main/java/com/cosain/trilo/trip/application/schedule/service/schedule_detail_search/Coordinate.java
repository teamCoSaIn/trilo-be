package com.cosain.trilo.trip.application.schedule.service.schedule_detail_search;

import lombok.Getter;

@Getter
public class Coordinate {
    private double latitude;
    private double longitude;

    public static Coordinate from(double latitude, double longitude){
        return new Coordinate(latitude, longitude);
    }

    private Coordinate(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
