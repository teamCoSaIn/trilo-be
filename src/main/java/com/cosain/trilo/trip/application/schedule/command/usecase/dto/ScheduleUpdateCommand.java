package com.cosain.trilo.trip.application.schedule.command.usecase.dto;

import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import lombok.Getter;

@Getter
public class ScheduleUpdateCommand {
    private ScheduleTitle scheduleTitle;
    private ScheduleContent scheduleContent;
    private ScheduleTime scheduleTime;

    public ScheduleUpdateCommand(ScheduleTitle scheduleTitle, ScheduleContent scheduleContent, ScheduleTime scheduleTime){
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
        this.scheduleTime = scheduleTime;
    }
}
