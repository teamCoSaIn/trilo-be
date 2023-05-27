package com.cosain.trilo.trip.infra.dto;

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
