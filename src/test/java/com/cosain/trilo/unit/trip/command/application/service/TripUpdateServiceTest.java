package com.cosain.trilo.unit.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.TripUpdateCommand;
import com.cosain.trilo.trip.command.application.service.TripUpdateService;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.cosain.trilo.fixture.TripFixture.DECIDED_TRIP;
import static com.cosain.trilo.fixture.TripFixture.UNDECIDED_TRIP;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripUpdateServiceTest {

    @InjectMocks
    private TripUpdateService tripUpdateService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private DayRepository dayRepository;

    @Test
    public void 여행_상태가_UNDECIDED이고_날짜와_제목을_수정할_때() throws Exception {
        // given
        TripUpdateCommand updateCommand = TripUpdateCommand.builder()
                .title("수정할 제목")
                .startDate(LocalDate.of(2023, 5, 5))
                .endDate(LocalDate.of(2023, 5, 15))
                .build();

        Trip trip = UNDECIDED_TRIP.createUndecided(1L, 1L, "여행 제목");
        given(tripRepository.findByIdWithDays(anyLong())).willReturn(Optional.of(trip));

        // when
        tripUpdateService.updateTrip(1L, 1L, updateCommand);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        verify(dayRepository, times(0)).deleteDays(anyList());
        verify(dayRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void 여행_상태가_DECIDED이고_다른_제목과_기간으로_수정하는_경우() throws Exception {
        // given
        TripUpdateCommand updateCommand = TripUpdateCommand.builder()
                .title("수정할 제목")
                .startDate(LocalDate.of(2023, 5, 5))
                .endDate(LocalDate.of(2023, 5, 15))
                .build();
        Trip trip = DECIDED_TRIP.createDecided(1L, 1L, "여행 제목", LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 20));
        given(tripRepository.findByIdWithDays(anyLong())).willReturn(Optional.of(trip));

        // when
        tripUpdateService.updateTrip(1L, 1L, updateCommand);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        verify(dayRepository, times(1)).deleteDays(anyList());
        verify(dayRepository, times(1)).saveAll(anyList());
    }

}
