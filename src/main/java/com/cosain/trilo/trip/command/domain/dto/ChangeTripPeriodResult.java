package com.cosain.trilo.trip.command.domain.dto;

import com.cosain.trilo.trip.command.domain.entity.Day;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChangeTripPeriodResult {

    private final List<Day> deletedDays = new ArrayList<>();
    private final List<Day> createdDays = new ArrayList<>();

    public static ChangeTripPeriodResult of(List<Day> deletedDays, List<Day> createdDays) {
        return new ChangeTripPeriodResult(deletedDays, createdDays);
    }

    private ChangeTripPeriodResult(List<Day> deletedDays, List<Day> createdDays) {
        this.deletedDays.addAll(deletedDays);
        this.createdDays.addAll(createdDays);
    }
}
