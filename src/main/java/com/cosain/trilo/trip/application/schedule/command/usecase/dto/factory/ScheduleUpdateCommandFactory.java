package com.cosain.trilo.trip.application.schedule.command.usecase.dto.factory;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleUpdateCommand;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleUpdateCommandFactory {

    public ScheduleUpdateCommand createCommand(String rawTitle, String rawContent) {
        List<CustomException> exceptions = new ArrayList<>();
        ScheduleTitle scheduleTitle = makeScheduleTitle(rawTitle, exceptions);
        ScheduleContent scheduleContent = makeScheduleContent(rawContent, exceptions);

        if (!exceptions.isEmpty()) {
            throw new CustomValidationException(exceptions);
        }
        return new ScheduleUpdateCommand(scheduleTitle, scheduleContent);
    }

    private ScheduleTitle makeScheduleTitle(String rawTitle, List<CustomException> exceptions) {
        try {
            return ScheduleTitle.of(rawTitle);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }

    private ScheduleContent makeScheduleContent(String rawContent, List<CustomException> exceptions) {
        try {
            return ScheduleContent.of(rawContent);
        } catch (CustomException e) {
            exceptions.add(e);
            return null;
        }
    }
}
