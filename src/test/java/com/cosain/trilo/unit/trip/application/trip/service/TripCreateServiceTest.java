package com.cosain.trilo.unit.trip.application.trip.service;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.trip.service.TripCreateService;
import com.cosain.trilo.trip.application.trip.dto.TripCreateCommand;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void create_and_repository_called() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        TripCreateCommand createCommand = new TripCreateCommand(TripTitle.of("제목"));

        // mocking
        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        given(tripRepository.save(any(Trip.class))).willReturn(trip);

        // when
        Long returnTripId = tripCreateService.createTrip(tripperId, createCommand);

        // then
        verify(tripRepository).save(any(Trip.class));
        assertThat(returnTripId).isEqualTo(tripId);
    }

}
