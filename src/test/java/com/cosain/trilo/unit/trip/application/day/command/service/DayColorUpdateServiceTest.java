package com.cosain.trilo.unit.trip.application.day.command.service;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.day.command.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.command.service.DayColorUpdateService;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoDayUpdateAuthorityException;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.vo.DayColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@DisplayName("Day 색상 변경 서비스 테스트")
public class DayColorUpdateServiceTest {

    @InjectMocks
    private DayColorUpdateService dayColorUpdateService;

    @Mock
    private DayRepository dayRepository;

    @Test
    @DisplayName("존재하지 않는 Day의 식별자 -> DayNotFoundException")
    public void dayNotFoundTest() {
        // given

        Long dayId = 1L;
        Long tripperId = 2L;
        String rawColorName = "RED";
        DayColorUpdateCommand updateCommand = new DayColorUpdateCommand(DayColor.of(rawColorName));

        given(dayRepository.findByIdWithTrip(eq(dayId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> dayColorUpdateService.updateDayColor(dayId, tripperId, updateCommand))
                .isInstanceOf(DayNotFoundException.class);

        verify(dayRepository, times(1)).findByIdWithTrip(eq(dayId));
    }

    @Test
    @DisplayName("권한 없는 사람이 색상 수정 시도 -> NoDayUpdateAuthorityException 발생")
    public void noDayUpdateAuthorityTest() {
        // given
        Long dayId = 1L;
        Long tripId = 2L;
        Long tripOwnerId = 3L;
        Long invalidTripperId = 4L;

        DayColor beforeDayColor = DayColor.BLACK;
        DayColor requestDayColor = DayColor.RED;
        Day day = fixtureDayForColorTest(tripId, tripOwnerId, beforeDayColor);
        DayColorUpdateCommand updateCommand = new DayColorUpdateCommand(requestDayColor);

        given(dayRepository.findByIdWithTrip(eq(dayId))).willReturn(Optional.of(day));

        // when & then
        assertThatThrownBy(() -> dayColorUpdateService.updateDayColor(dayId, invalidTripperId, updateCommand))
                .isInstanceOf(NoDayUpdateAuthorityException.class);

        verify(dayRepository, times(1)).findByIdWithTrip(eq(dayId));
    }

    @Test
    @DisplayName("색상 수정 성공 테스트")
    public void successTest() {
        Long dayId = 1L;
        Long tripId = 2L;
        Long tripOwnerId = 3L;

        DayColor beforeDayColor = DayColor.BLACK;
        DayColor requestDayColor = DayColor.RED;

        Day day = fixtureDayForColorTest(tripId, tripOwnerId, beforeDayColor);
        DayColorUpdateCommand updateCommand = new DayColorUpdateCommand(requestDayColor);

        given(dayRepository.findByIdWithTrip(eq(dayId))).willReturn(Optional.of(day));

        // when
        dayColorUpdateService.updateDayColor(dayId, tripOwnerId, updateCommand);

        // then
        verify(dayRepository, times(1)).findByIdWithTrip(eq(dayId));
        assertThat(day.getDayColor()).isSameAs(requestDayColor);
    }

    private Day fixtureDayForColorTest(Long tripId, Long tripperId, DayColor dayColor) {
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);
        Trip trip = TripFixture.decided_Id_Color(tripId, tripperId, startDate, endDate, 1L, dayColor);
        return trip.getDays().get(0);
    }

}
