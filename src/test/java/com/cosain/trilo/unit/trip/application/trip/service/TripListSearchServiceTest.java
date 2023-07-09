package com.cosain.trilo.unit.trip.application.trip.service;

import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.application.trip.service.TripListSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepository;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripPageCondition;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @DisplayName("여행자(사용자) 여행 목록 조회 성공 테스트 : 의존성 호출 여부")
    void searchTripSummariesTest(){
        // given
        Long tripperId = 1L;
        Long standardTripId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        String imageName = "image.jpg";
        String imageURL = "https://.../image.jpg";
        TripSummary tripSummary1 = new TripSummary(2L, tripperId, "여행 1", TripStatus.DECIDED, LocalDate.of(2023,5,1), LocalDate.of(2023,5,1), imageName);
        TripSummary tripSummary2 = new TripSummary(1L, tripperId, "여행 2", TripStatus.UNDECIDED, null, null, imageName);

        Slice<TripSummary> tripSummaries = new PageImpl<>(List.of(tripSummary1, tripSummary2));

        TripPageCondition tripPageCondition = new TripPageCondition(tripperId, standardTripId);
        given(userRepository.findById(eq(tripperId))).willReturn(Optional.of(KAKAO_MEMBER.create()));
        given(tripQueryRepository.findTripSummariesByTripperId(any(TripPageCondition.class), any(Pageable.class))).willReturn(tripSummaries);
        given(tripImageOutputAdapter.getFullTripImageURL(eq(imageName))).willReturn(imageURL);

        // when
        Slice<TripSummary> searchTripSummaries = tripListSearchService.searchTripSummaries(tripPageCondition, pageable);

        // then
        assertThat(searchTripSummaries).isNotNull();
        assertThat(searchTripSummaries.getContent()).hasSize(2);
        assertThat(searchTripSummaries.getContent().get(0).getTitle()).isEqualTo(tripSummary1.getTitle());
        assertThat(searchTripSummaries.getContent().get(1).getTitle()).isEqualTo(tripSummary2.getTitle());
        assertThat(searchTripSummaries.getContent()).map(TripSummary::getImageURL).allMatch(url -> url.equals(imageURL));
        assertThat(searchTripSummaries.hasNext()).isFalse();

        verify(userRepository, times(1)).findById(eq(tripperId));
        verify(tripQueryRepository, times(1)).findTripSummariesByTripperId(any(TripPageCondition.class), any(Pageable.class));
        verify(tripImageOutputAdapter, times(2)).getFullTripImageURL(anyString());
    }

    @Test
    @DisplayName("tripperId에 해당하는 사용자가 존재하지 않으면 TripperNotFoundException 예외 발생")
    void when_the_user_is_not_exist_that_coincide_with_tripper_id_it_will_throws_TripperNotFoundException(){
        // given
        Long tripperId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        TripPageCondition tripPageCondition = new TripPageCondition(1L, 1L);
        given(userRepository.findById(tripperId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripListSearchService.searchTripSummaries(tripPageCondition, pageable))
                .isInstanceOf(TripperNotFoundException.class);
    }

}
