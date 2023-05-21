package com.cosain.trilo.unit.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.service.dto.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.command.service.TripCreateService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] TripCreateService에서")
public class TripCreateServiceTest {

    @InjectMocks
    private TripCreateService tripCreateService;

    @Mock
    private TripRepository tripRepository;

    @Test
    @DisplayName("create 하면, 내부적으로 repository가 호출된다.")
    public void create_and_repository_called() throws Exception {
        // given
        TripCreateCommand createCommand = new TripCreateCommand("제목");
        Long tripperId = 1L;

        // mocking
        Trip trip = Trip.create("제목", tripperId);
        injectFakeTripId(trip, 1L);

        given(tripRepository.save(any(Trip.class))).willReturn(trip);

        // when
        tripCreateService.createTrip(tripperId, createCommand);

        // then
        verify(tripRepository).save(any(Trip.class));
    }

    private void injectFakeTripId(Trip trip, Long fakeTripId) {
        Field field = ReflectionUtils.findField(Trip.class, "id");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, trip, fakeTripId);
    }

}
