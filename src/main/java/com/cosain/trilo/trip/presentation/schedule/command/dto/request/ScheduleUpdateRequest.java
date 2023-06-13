package com.cosain.trilo.trip.presentation.schedule.command.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleUpdateRequest {

    private String title;
    private String content;
    private LocalTime startTime;
    private LocalTime endTime;
}
