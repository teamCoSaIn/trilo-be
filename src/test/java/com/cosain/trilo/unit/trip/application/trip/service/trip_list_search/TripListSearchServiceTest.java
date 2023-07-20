package com.cosain.trilo.unit.trip.application.trip.service.trip_list_search;

import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.exception.TripperNotFoundException;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchService;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.user.domain.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripListSearchServiceTest {
    @InjectMocks
    private TripListSearchService tripListSearchService;

    @Mock
    private TripQueryDAO tripQueryDAO;

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
        int pageSize = 10;
        TripListQueryParam queryParam = TripListQueryParam.of(tripperId, standardTripId, pageSize);

//        Pageable pageable = PageRequest.of(0, 10);
        String imageName = "image.jpg";
        String imageURL = "https://.../image.jpg";
        TripListSearchResult.TripSummary tripSummary1 = new TripListSearchResult.TripSummary(2L, tripperId, "여행 1", TripStatus.DECIDED, LocalDate.of(2023,5,1), LocalDate.of(2023,5,1), imageName);
        TripListSearchResult.TripSummary tripSummary2 = new TripListSearchResult.TripSummary(1L, tripperId, "여행 2", TripStatus.UNDECIDED, null, null, imageName);
        TripListSearchResult result = TripListSearchResult.of(false, List.of(tripSummary1, tripSummary2));

        given(userRepository.findById(eq(tripperId))).willReturn(Optional.of(UserFixture.kakaoUser_Id(tripperId)));
        given(tripQueryDAO.findTripSummariesByTripperId(eq(queryParam))).willReturn(result);
        given(tripImageOutputAdapter.getFullTripImageURL(eq(imageName))).willReturn(imageURL);

        // when
        TripListSearchResult searchResult = tripListSearchService.searchTripList(queryParam);

        // then
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getTrips()).hasSize(2);
        assertThat(searchResult.getTrips().get(0).getTripId()).isEqualTo(tripSummary1.getTripId());
        assertThat(searchResult.getTrips().get(1).getTripId()).isEqualTo(tripSummary2.getTripId());
        assertThat(searchResult.getTrips()).map(TripListSearchResult.TripSummary::getImageURL).allMatch(url -> url.equals(imageURL));
        assertThat(searchResult.isHasNext()).isFalse();

        verify(userRepository, times(1)).findById(eq(tripperId));
        verify(tripQueryDAO, times(1)).findTripSummariesByTripperId(eq(queryParam));
        verify(tripImageOutputAdapter, times(2)).getFullTripImageURL(anyString());
    }

    @Test
    @DisplayName("tripperId에 해당하는 사용자가 존재하지 않으면 TripperNotFoundException 예외 발생")
    void when_the_user_is_not_exist_that_coincide_with_tripper_id_it_will_throws_TripperNotFoundException(){
        // given
        Long tripperId = 1L;
        Long tripId = 2L;
        int pageSize = 10;
        TripListQueryParam queryParam = TripListQueryParam.of(tripperId, tripId, pageSize);
        given(userRepository.findById(tripperId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripListSearchService.searchTripList(queryParam))
                .isInstanceOf(TripperNotFoundException.class);
    }

}
