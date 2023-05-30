package com.cosain.trilo.trip.application.schedule.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import lombok.Getter;

@Getter
public class ScheduleUpdateCommand {
    private ScheduleTitle scheduleTitle;
    private String content;

    public static ScheduleUpdateCommand of(String title, String content){
        return new ScheduleUpdateCommand(ScheduleTitle.of(title), content);
    }

    private ScheduleUpdateCommand(ScheduleTitle scheduleTitle, String content){
        this.scheduleTitle = scheduleTitle;
        this.content = content;
    }
}
