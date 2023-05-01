package com.cosain.trilo.trip.command.presentation.schedule.dto;

import com.cosain.trilo.trip.command.application.command.ScheduleUpdateCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleUpdateRequest {
    private String title;
    private String content;

    public ScheduleUpdateCommand toCommand(){
        return ScheduleUpdateCommand.of(this.title, this.content);
    }
}
