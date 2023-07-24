package com.cosain.trilo.trip.application.schedule.service.schedule_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(of = {"scheduleId", "requestTripperId", "scheduleTitle", "scheduleContent", "scheduleTime"})
public class ScheduleUpdateCommand {

    private final long scheduleId;
    private final long requestTripperId;
    private final ScheduleTitle scheduleTitle;
    private final ScheduleContent scheduleContent;
    private final ScheduleTime scheduleTime;

    public static ScheduleUpdateCommand of(
            long scheduleId, long requestTripperId,
            String rawScheduleTitle, String rawScheduleContent, LocalTime startTime, LocalTime endTime) {

        List<CustomException> exceptions = new ArrayList<>();
        ScheduleTitle scheduleTitle = makeScheduleTitle(rawScheduleTitle, exceptions);
        ScheduleContent scheduleContent = makeScheduleContent(rawScheduleContent, exceptions);
        ScheduleTime scheduleTime = makeScheduleTime(startTime, endTime, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new ScheduleUpdateCommand(scheduleId, requestTripperId, scheduleTitle, scheduleContent, scheduleTime);
    }

    private static ScheduleTitle makeScheduleTitle(String rawTitle, List<CustomException> exceptions) {
        try {
            return ScheduleTitle.of(rawTitle);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    private static ScheduleContent makeScheduleContent(String rawContent, List<CustomException> exceptions) {
        try {
            return ScheduleContent.of(rawContent);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    private static ScheduleTime makeScheduleTime(LocalTime startTime, LocalTime endTime, List<CustomException> exceptions) {
        try {
            return ScheduleTime.of(startTime, endTime);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    private ScheduleUpdateCommand(long scheduleId, long requestTripperId, ScheduleTitle scheduleTitle, ScheduleContent scheduleContent, ScheduleTime scheduleTime) {
        this.scheduleId = scheduleId;
        this.requestTripperId = requestTripperId;
        this.scheduleTitle = scheduleTitle;
        this.scheduleContent = scheduleContent;
        this.scheduleTime = scheduleTime;
    }
}
