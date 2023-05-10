package com.cosain.trilo.unit.trip.query.application;

import com.cosain.trilo.trip.query.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.query.application.service.ScheduleDetailSearchService;
import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.query.presentation.schedule.dto.ScheduleDetailResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        ScheduleDetail scheduleDetail = new ScheduleDetail(1L, 1L, "제목", "장소 이름", 24.24, 24.24, 3L, "내용");
        given(scheduleQueryRepository.findScheduleDetailByScheduleId(anyLong())).willReturn(Optional.of(scheduleDetail));

        // when
        ScheduleDetailResponse scheduleDetailResponse = scheduleDetailSearchService.searchScheduleDetail(anyLong());

        // then
        Assertions.assertThat(scheduleDetailResponse.getScheduleId()).isEqualTo(scheduleDetail.getScheduleId());

    }

    @Test
    @DisplayName("조회한 일정이 없을 경우 ScheduleNotFoundException 이 발생한다 ")
    void searchScheduleDetail_Fail_ScheduleNotFound() {
        // given
        given(scheduleQueryRepository.findScheduleDetailByScheduleId(anyLong())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> scheduleDetailSearchService.searchScheduleDetail(1L)).isInstanceOf(ScheduleNotFoundException.class);
    }
}
