package com.cosain.trilo.unit.trip.application.schedule.service;

import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.service.ScheduleDetailSearchService;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("[ScheduleQuery] 일정 단건 조회 응용서비스 테스트")
public class ScheduleDetailSearchServiceTest {

    @InjectMocks
    private ScheduleDetailSearchService scheduleDetailSearchService;

    @Mock
    private ScheduleQueryRepository scheduleQueryRepository;

    @Test
    @DisplayName("정상 호출 및 반환 테스트")
    void searchScheduleDetailTest(){
        // given
        ScheduleDetail scheduleDetail = new ScheduleDetail(1L, 1L, "제목", "장소 이름", 24.24, 24.24, 3L, "내용", LocalTime.of(15, 30), LocalTime.of(16, 0));
        given(scheduleQueryRepository.findScheduleDetailById(anyLong())).willReturn(Optional.of(scheduleDetail));

        // when
        ScheduleDetail dto = scheduleDetailSearchService.searchScheduleDetail(anyLong());

        // then
        assertThat(dto.getScheduleId()).isEqualTo(scheduleDetail.getScheduleId());
        assertThat(dto.getDayId()).isEqualTo(scheduleDetail.getDayId());
        assertThat(dto.getContent()).isEqualTo(scheduleDetail.getContent());
        assertThat(dto.getTitle()).isEqualTo(scheduleDetail.getTitle());
        assertThat(dto.getCoordinate().getLatitude()).isEqualTo(scheduleDetail.getCoordinate().getLatitude());
        assertThat(dto.getCoordinate().getLongitude()).isEqualTo(scheduleDetail.getCoordinate().getLongitude());
        assertThat(dto.getOrder()).isEqualTo(scheduleDetail.getOrder());
        assertThat(dto.getPlaceName()).isEqualTo(scheduleDetail.getPlaceName());



    }

    @Test
    @DisplayName("조회한 일정이 없을 경우 ScheduleNotFoundException 이 발생한다 ")
    void searchScheduleDetail_Fail_ScheduleNotFound() {
        // given
        given(scheduleQueryRepository.findScheduleDetailById(anyLong())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> scheduleDetailSearchService.searchScheduleDetail(1L)).isInstanceOf(ScheduleNotFoundException.class);
    }
}
