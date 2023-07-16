package com.cosain.trilo.unit.trip.application.trip.service;

import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.application.exception.NoTripDetailSearchAuthorityException;
import com.cosain.trilo.trip.application.trip.service.TripDetailSearchService;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[TripQuery] 여행 단건 조회 응용서비스 테스트")
public class TripDetailSearchServiceTest {

    @InjectMocks
    private TripDetailSearchService tripDetailSearchService;

    @Mock
    private TripQueryRepository tripQueryRepository;

    @Test
    @DisplayName("정상 호출 시에 호출 및 반환 결과 테스트")
    void called_test(){
        // given
        TripDetail tripDetail = new TripDetail(1L, 1L, "제목", TripStatus.DECIDED, LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 15));
        given(tripQueryRepository.findTripDetailById(anyLong())).willReturn(Optional.of(tripDetail));

        // when
        TripDetail dto = tripDetailSearchService.searchTripDetail( 1L);

        // then
        verify(tripQueryRepository).findTripDetailById(anyLong());
        assertThat(dto.getTripId()).isEqualTo(tripDetail.getTripId());
        assertThat(dto.getStatus()).isEqualTo(tripDetail.getStatus());
        assertThat(dto.getStartDate()).isEqualTo(tripDetail.getStartDate());
        assertThat(dto.getEndDate()).isEqualTo(tripDetail.getEndDate());
    }

    @Test
    @DisplayName("조회한 Trip이 없을 경우 TripNotFoundException 이 발생한다.")
    void when_the_trip_is_not_exist_is_throws_TripNotFoundException(){
        // given
        given(tripQueryRepository.findTripDetailById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripDetailSearchService.searchTripDetail( 1L))
                .isInstanceOf(TripNotFoundException.class);
    }
}
