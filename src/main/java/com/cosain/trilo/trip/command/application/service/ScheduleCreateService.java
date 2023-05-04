package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;
import com.cosain.trilo.trip.command.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.command.application.exception.NoScheduleCreateAuthorityException;
import com.cosain.trilo.trip.command.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.command.application.usecase.ScheduleCreateUseCase;
import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
@Service
public class ScheduleCreateService implements ScheduleCreateUseCase {

    private final ScheduleRepository scheduleRepository;
    private final DayRepository dayRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public Long createSchedule(Long tripperId, ScheduleCreateCommand createCommand) {
        /**
         * TODO : 임시보관함 고려해서 수정하기
         */
        Day day = findDay(createCommand.getDayId());
        Trip trip = findTrip(createCommand.getTripId());
        validateCreateAuthority(trip, tripperId);

        // TODO: Schedule 생성의 책임을 Trip 및 Day에게 위임
        // Schedule 생성 시 create를 통해 생성하는데, 컴파일 에러를 막기 위해 임시방편으로 현재 시각의 EpochSecond를 이용해 랜덤 순서값을 부여하도록 했다.
        Schedule schedule = Schedule.create(day, trip, createCommand.getTitle(), createCommand.getPlace(), ScheduleIndex.of(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)%1_000_000_000));

        scheduleRepository.save(schedule);

        return schedule.getId();
    }

    private Day findDay(Long dayId){
        return dayRepository.findById(dayId).orElseThrow(() -> new DayNotFoundException("Schedule을 Day에 넣으려고 했는데, 해당하는 Day가 존재하지 않음."));
    }

    private Trip findTrip(Long tripId){
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("trip이 존재하지 않음"));
    }

    private void validateCreateAuthority(Trip trip, Long tripperId) {
        if(!trip.getTripperId().equals(tripperId)){
            throw new NoScheduleCreateAuthorityException("여행의 소유주가 아닌 사람이, 일정을 생성하려고 함");
        }
    }

}
