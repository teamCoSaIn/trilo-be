package com.cosain.trilo.unit.trip.application.trip.service.trip_create;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_create.TripCreateService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 여행 생성을 담당하는 여행 생성 서비스({@link TripCreateService})의 테스트 코드입니다.
 * @see TripCreateService
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("여행 생성 서비스 테스트")
public class TripCreateServiceTest {

    /**
     * 테스트 대상이 되는 여행 생성 서비스입니다.
     */
    @InjectMocks
    private TripCreateService tripCreateService;

    /**
     * TripCreateService의 의존성
     */
    @Mock
    private TripRepository tripRepository;

    /**
     * 여행 생성 서비스 기능을 테스트하고 의도한 대로 동작하는 지 테스트합니다.
     * <ul>
     *     <li>내부 의존성이 잘 호출됐는 지 검증해야합니다.</li>
     *     <li>리포지토리에서 발급받은 여행({@link Trip})의 식별자가 반환되는 지 검증합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("create 하면, 내부적으로 repository가 호출된다.")
    public void create_and_repository_called() {
        // given
        Long tripId = 1L;
        Long requestTripperId = 2L;
        var command = TripCreateCommand.of(requestTripperId, "제목");

        Trip savedTrip = TripFixture.undecided_Id(tripId, requestTripperId);
        given(tripRepository.save(any(Trip.class))).willReturn(savedTrip); // 리포지토리에서 가져올 저장된 여행 mocking

        // when
        Long returnTripId = tripCreateService.createTrip(command);

        // then
        verify(tripRepository, times(1)).save(any(Trip.class));
        assertThat(returnTripId).isEqualTo(tripId);
    }

}
