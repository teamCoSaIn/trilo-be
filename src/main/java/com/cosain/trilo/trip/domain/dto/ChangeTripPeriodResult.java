package com.cosain.trilo.trip.domain.dto;

import com.cosain.trilo.trip.domain.entity.Day;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChangeTripPeriodResult {

    private final List<Long> deletedDayIds = new ArrayList<>();
    private final List<Day> createdDays = new ArrayList<>();

    public static ChangeTripPeriodResult of(List<Day> deletedDays, List<Day> createdDays) {
        List<Long> deleteDayIds = deletedDays.stream()
                .map(Day::getId)
                .toList();

        return new ChangeTripPeriodResult(deleteDayIds, createdDays);
    }

    private ChangeTripPeriodResult(List<Long> deletedDayIds, List<Day> createdDays) {
        this.deletedDayIds.addAll(deletedDayIds);
        this.createdDays.addAll(createdDays);
    }
}
