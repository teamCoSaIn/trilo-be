package com.cosain.trilo.unit.trip.application.trip.service.trip_title_update;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.service.trip_title_update.TripTitleUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_title_update.TripTitleUpdateService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripTitleUpdateService(여행 제목 변경 서비스) 테스트")
public class TripTitleUpdateServiceTest {

    @InjectMocks
    private TripTitleUpdateService tripTitleUpdateService;

    @Mock
    private TripRepository tripRepository;

    @Test
    @DisplayName("여행 제목 변경 성공 테스트")
    public void successTest() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        String beforeTitle = "여행 제목";
        String requestTitle = "수정 여행 제목";

        TripTitleUpdateCommand updateCommand = createCommand(requestTitle);
        Trip trip = TripFixture.undecided_Id_Title(tripId, tripperId, beforeTitle);

        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // when
        tripTitleUpdateService.updateTripTitle(tripId, tripperId, updateCommand);

        // then
        verify(tripRepository, times(1)).findById(eq(tripId));
        assertThat(trip.getTripTitle().getValue()).isEqualTo(requestTitle);
    }

    @Test
    @DisplayName("여행 존재 안 함 -> TripNotFoundException 발생")
    public void if_update_not_exist_trip_tripTitle_then_it_throws_TripNotFoundException() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        String requestTitle = "수정 여행 제목";

        TripTitleUpdateCommand updateCommand = createCommand(requestTitle);

        // mock
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripTitleUpdateService.updateTripTitle(tripId, tripperId, updateCommand))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository, times(1)).findById(eq(tripId));
    }

    @DisplayName("여행의 소유자가 아닌 사람 -> NoTripUpdateAuthorityException 발생")
    @Test
    void noTripUpdateAuthorityTest() {
        // given
        Long tripId = 1L;
        Long realTripOwnerId = 2L;
        Long noAuthorityTripperId = 4L;

        String beforeTitle = "여행 제목";
        String requestTitle = "수정 여행 제목";

        TripTitleUpdateCommand updateCommand = createCommand(requestTitle);
        Trip trip = TripFixture.undecided_Id_Title(tripId, realTripOwnerId, beforeTitle);

        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() -> tripTitleUpdateService.updateTripTitle(tripId, noAuthorityTripperId, updateCommand))
                .isInstanceOf(NoTripUpdateAuthorityException.class);

        verify(tripRepository, times(1)).findById(eq(tripId));
    }

    private TripTitleUpdateCommand createCommand(String rawTitle) {
        return new TripTitleUpdateCommand(TripTitle.of(rawTitle));
    }

}
