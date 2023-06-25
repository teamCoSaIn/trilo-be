package com.cosain.trilo.unit.trip.application.trip.query.service;

import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.application.trip.query.service.TripListSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
import com.cosain.trilo.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.cosain.trilo.fixture.UserFixture.KAKAO_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TripListSearchServiceTest {
    @InjectMocks
    private TripListSearchService tripListSearchService;

    @Mock
    private TripQueryRepository tripQueryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripImageOutputAdapter tripImageOutputAdapter;

    @Test
    @DisplayName("정상 호출 시에 호출 및 반환 테스트")
    void searchTripDetailsTest(){
        // given
        Long tripperId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        TripSummary tripSummary1 = new TripSummary(1L, tripperId, "여행 1", TripStatus.DECIDED, LocalDate.now(), LocalDate.now(), "image.jpg");
        TripSummary tripSummary2 = new TripSummary(2L, tripperId, "여행 2", TripStatus.UNDECIDED, LocalDate.now(), LocalDate.now(), "image.jpg");

        Slice<TripSummary> tripSummaries = new PageImpl<>(List.of(tripSummary1, tripSummary2));

        TripPageCondition tripPageCondition = new TripPageCondition(1L, 1L);
        given(tripImageOutputAdapter.getTripImageFullPath(anyString())).willReturn("ImageFullPath");
        given(userRepository.findById(eq(1L))).willReturn(Optional.of(KAKAO_MEMBER.create()));
        given(tripQueryRepository.findTripSummariesByTripperId(tripPageCondition, pageable)).willReturn(tripSummaries);

        // when
        Slice<TripSummary> searchTripSummaries = tripListSearchService.searchTripSummaries(tripPageCondition, pageable);

        // then
        assertThat(searchTripSummaries).isNotNull();
        assertThat(searchTripSummaries.getContent()).hasSize(2);
        assertThat(searchTripSummaries.getContent().get(0).getTitle()).isEqualTo(tripSummary1.getTitle());
        assertThat(searchTripSummaries.getContent().get(1).getTitle()).isEqualTo(tripSummary2.getTitle());
        assertThat(searchTripSummaries.hasNext()).isFalse();

    }

    @Test
    @DisplayName("tripperId에 해당하는 사용자가 존재하지 않으면 TripperNotFoundException 에러를 반환한다.")
    void when_the_user_is_not_exist_that_coincide_with_tripper_id_it_will_throws_TripperNotFoundException(){
        // given
        Long tripperId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        TripPageCondition tripPageCondition = new TripPageCondition(1L, 1L);
        given(userRepository.findById(tripperId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripListSearchService.searchTripSummaries(tripPageCondition, pageable)).isInstanceOf(TripperNotFoundException.class);

    }

}
