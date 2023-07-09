package com.cosain.trilo.trip.application.day.dto;

import com.cosain.trilo.trip.domain.vo.DayColor;
import lombok.Getter;

@Getter
public class DayColorUpdateCommand {

    private DayColor dayColor;

    public DayColorUpdateCommand(DayColor dayColor) {
        this.dayColor = dayColor;
    }
}
