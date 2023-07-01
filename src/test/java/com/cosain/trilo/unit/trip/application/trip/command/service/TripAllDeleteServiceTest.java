package com.cosain.trilo.unit.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.service.TripAllDeleteService;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripAllDeleteServiceTest {

    @InjectMocks
    private TripAllDeleteService tripAllDeleteService;

    @Mock
    private TripRepository tripRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private DayRepository dayRepository;

    @Test
    void 메서드_호출_테스트(){
        // given
        Long tripperId = 1L;

        // when
        tripAllDeleteService.deleteAllByTripperId(tripperId);

        // then
        verify(tripRepository).findAllByTripperId(any());
        verify(tripRepository).deleteAllByTripperId(any());
        verify(dayRepository).deleteAllByTripIds(any());
        verify(scheduleRepository).deleteAllByTripIds(any());
    }
}
