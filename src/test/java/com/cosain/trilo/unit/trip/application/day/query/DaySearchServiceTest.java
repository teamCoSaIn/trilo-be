package com.cosain.trilo.unit.trip.application.day.query;

import com.cosain.trilo.trip.application.day.query.service.DaySearchService;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.query.domain.repository.DayQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.DayScheduleDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Day 단건 조회 응용 서비스 테스트")
public class DaySearchServiceTest {

    @InjectMocks
    private DaySearchService daySearchService;

    @Mock
    private DayQueryRepository dayQueryRepository;

    @Test
    void Day_단건_조회(){

        // given
        Long dayId = 1L;
        DayScheduleDetail.ScheduleSummary scheduleSummary = new DayScheduleDetail.ScheduleSummary(1L, "제목", "장소", 33.33, 33.33);
        DayScheduleDetail dayScheduleDetail = new DayScheduleDetail(dayId, 1L, LocalDate.of(2023, 5, 5), List.of(scheduleSummary));
        given(dayQueryRepository.findDayWithSchedulesByDayId(1L)).willReturn(Optional.of(dayScheduleDetail));

        // when
        DayScheduleDetail findDayScheduleDetail = daySearchService.searchDeySchedule(1L);
        // then
        assertThat(findDayScheduleDetail.getDayId()).isEqualTo(dayId);
        verify(dayQueryRepository, times(1)).findDayWithSchedulesByDayId(dayId);
    }

    @Test
    void Day_단건_조회시_찾으려는_Day_가_존재하지_않는다면_DayNotFoundException예외가_발생한다(){

        // given
        Long dayId = 1L;
        given(dayQueryRepository.findDayWithSchedulesByDayId(dayId)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> daySearchService.searchDeySchedule(dayId)).isInstanceOf(DayNotFoundException.class);
    }


}
