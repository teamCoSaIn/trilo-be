package com.cosain.trilo.trip.command.application.command;

import lombok.Getter;

@Getter
public class ScheduleUpdateCommand {
    private String title;
    private String content;

    public static ScheduleUpdateCommand of(String title, String content){
        return new ScheduleUpdateCommand(title, content);
    }

    private ScheduleUpdateCommand(String title, String content){
        this.title = title;
        this.content = content;
    }
}
