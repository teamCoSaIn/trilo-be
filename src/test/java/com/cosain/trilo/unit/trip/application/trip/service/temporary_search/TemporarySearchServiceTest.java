package com.cosain.trilo.unit.trip.application.trip.service.temporary_search;

import com.cosain.trilo.trip.application.dao.ScheduleQueryDAO;
import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListQueryParam;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListSearchResult;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TemporarySearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TemporarySearchServiceTest {

    @InjectMocks
    private TemporarySearchService temporarySearchService;

    @Mock
    private TripQueryDAO tripQueryDAO;

    @Mock
    private ScheduleQueryDAO scheduleQueryDAO;

    @Test
    void tripId가_유효하고_임시보관함이_비어있지_않으면_페이지_요청의_크기만큼의_임시보관함_일정목록을_반환한다(){
        // given
        int size = 3;
        Long tripId = 1L;
        long scheduleId = 1L;

        ScheduleSummary scheduleSummary1 = new ScheduleSummary(2L, "일정 제목1", "제목","장소 식별자", 33.33, 33.33);
        ScheduleSummary scheduleSummary2 = new ScheduleSummary(3L, "일정 제목2", "제목","장소 식별자",33.33, 33.33);
        ScheduleSummary scheduleSummary3 = new ScheduleSummary(4L, "일정 제목3", "제목","장소 식별자",33.33, 33.33);

        var queryParam = TempScheduleListQueryParam.of(tripId, scheduleId, size);
        var result = TempScheduleListSearchResult.of(true, List.of(scheduleSummary1, scheduleSummary2, scheduleSummary3));

        given(tripQueryDAO.existById(eq(tripId))).willReturn(true);
        given(scheduleQueryDAO.existById(eq(scheduleId))).willReturn(true);
        given(scheduleQueryDAO.findTemporarySchedules(eq(queryParam))).willReturn(result);

        // when
        var returnResult = temporarySearchService.searchTemporary(queryParam);

        // then
        assertThat(returnResult.isHasNext()).isEqualTo(result.isHasNext());
        assertThat(returnResult.getTempSchedules().size()).isEqualTo(3);
        assertThat(returnResult.getTempSchedules().get(0).getScheduleId()).isEqualTo(scheduleSummary1.getScheduleId());
        assertThat(returnResult.getTempSchedules().get(1).getScheduleId()).isEqualTo(scheduleSummary2.getScheduleId());
        assertThat(returnResult.getTempSchedules().get(2).getScheduleId()).isEqualTo(scheduleSummary3.getScheduleId());
    }
    @Test
    void tripId가_유효하지_않다면_TripNotFoundException을_발생시킨다(){
        // given
        long tripId = 1L;
        long scheduleId = 1L;
        int size = 3;

        var queryParam = TempScheduleListQueryParam.of(tripId, scheduleId, size);
        given(tripQueryDAO.existById(eq(tripId))).willReturn(false);

        // when & then
        assertThatThrownBy(() -> temporarySearchService.searchTemporary(queryParam))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void scheduleId에_해당하는_Schedule이_존재하지_않는다면_ScheduleNotFoundException_에러를_발생시킨다(){
        // given
        long tripId = 1L;
        long scheduleId = 1L;
        int size = 3;

        var queryParam = TempScheduleListQueryParam.of(tripId, scheduleId, size);
        given(tripQueryDAO.existById(eq(tripId))).willReturn(true);
        given(scheduleQueryDAO.existById(eq(scheduleId))).willReturn(false);


        // when & then
        assertThatThrownBy(() -> temporarySearchService.searchTemporary(queryParam))
                .isInstanceOf(ScheduleNotFoundException.class);

    }

}
