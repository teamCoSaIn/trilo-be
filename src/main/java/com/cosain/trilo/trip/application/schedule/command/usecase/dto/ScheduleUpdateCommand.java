package com.cosain.trilo.trip.application.schedule.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import lombok.Getter;

@Getter
public class ScheduleUpdateCommand {
    private ScheduleTitle scheduleTitle;
    private ScheduleContent scheduleContent;

    public static ScheduleUpdateCommand of(String title, String content){
        return new ScheduleUpdateCommand(ScheduleTitle.of(title), ScheduleContent.of(content));
    }

    private ScheduleUpdateCommand(ScheduleTitle scheduleTitle, ScheduleContent scheduleContent){
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
    }
}
