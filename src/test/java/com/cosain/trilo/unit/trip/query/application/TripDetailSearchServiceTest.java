package com.cosain.trilo.unit.trip.query.application;

import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import com.cosain.trilo.trip.query.application.dto.TripResult;
import com.cosain.trilo.trip.query.application.exception.NoTripDetailSearchAuthorityException;
import com.cosain.trilo.trip.query.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.query.application.service.TripDetailSearchService;
import com.cosain.trilo.trip.query.domain.dto.TripDto;
import com.cosain.trilo.trip.query.domain.repository.TripQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
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
        TripDto tripDto = TripDto.from(tripDetail);
        given(tripQueryRepository.findTripDetailByTripId(anyLong())).willReturn(Optional.of(tripDto));

        // when
        TripResult tripResult = tripDetailSearchService.searchTripDetail(1L, 1L);

        // then
        verify(tripQueryRepository).findTripDetailByTripId(anyLong());
        assertThat(tripResult.getId()).isEqualTo(tripDetail.getId());
        assertThat(tripResult.getStatus()).isEqualTo(tripDetail.getStatus());
        assertThat(tripResult.getStartDate()).isEqualTo(tripDetail.getStartDate());
        assertThat(tripResult.getEndDate()).isEqualTo(tripDetail.getEndDate());
    }

    @Test
    @DisplayName("조회 자격이 없는 사람이 요청할 경우 NoTripDetailSearchAuthorityException 이 발생한다")
    void when_no_authority_tripper_request_trip_detail_it_throws_NoTripDetailSearchAuthorityException(){

        // given
        TripDetail tripDetail = new TripDetail(1L, 1L, "제목", TripStatus.DECIDED, LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 15));
        TripDto tripDto = TripDto.from(tripDetail);
        given(tripQueryRepository.findTripDetailByTripId(anyLong())).willReturn(Optional.of(tripDto));

        // when && then
        assertThatThrownBy(() -> tripDetailSearchService.searchTripDetail(1L, 2L))
                .isInstanceOf(NoTripDetailSearchAuthorityException.class);

    }

    @Test
    @DisplayName("조회한 Trip이 없을 경우 TripNotFoundException 이 발생한다.")
    void when_the_trip_is_not_exist_is_throws_TripNotFoundException(){
        // given
        given(tripQueryRepository.findTripDetailByTripId(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripDetailSearchService.searchTripDetail(2L, 1L))
                .isInstanceOf(TripNotFoundException.class);
    }
}
