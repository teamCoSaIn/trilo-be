package com.cosain.trilo.unit.trip.application.trip.service.trip_like;

import com.cosain.trilo.common.exception.trip.LikeNotFoundException;
import com.cosain.trilo.common.exception.trip.TripAlreadyLikedException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.trip.service.trip_like.TripLikeService;
import com.cosain.trilo.trip.domain.entity.Like;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.LikeRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class TripLikeServiceTest {
    @InjectMocks
    private TripLikeService tripLikeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private TripRepository tripRepository;

    @Nested
    class 좋아요_테스트{
        @Test
        void 성공시_메서드_호출_테스트(){
            // given
            Long tripId = 1L;
            Long tripperId = 1L;
            Trip trip = TripFixture.undecided_Id(tripId, tripperId);
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
            given(likeRepository.existsByTripIdAndTripperId(eq(tripId), eq(tripperId))).willReturn(false);

            // when
            tripLikeService.addLike(tripId, tripperId);

            // then
            verify(likeRepository, times(1)).save(any(Like.class));
            verify(tripRepository, times(1)).findById(tripId);
        }

        @Test
        void 이미_좋아요를_했던_여행을_다시_좋아요할_경우_TripAlreadyLikedException_예외가_발생한다(){
            // given
            Long tripId = 1L;
            Long tripperId = 1L;
            given(likeRepository.existsByTripIdAndTripperId(eq(tripId), eq(tripperId))).willReturn(true);

            // when & then
            assertThatThrownBy(() -> tripLikeService.addLike(tripId, tripperId)).isInstanceOf(TripAlreadyLikedException.class);

        }

        @Test
        void 존재하지_않는_여행일_경우_TripNotFoundException_예외가_발생한다(){
            // given
            Long tripId = 1L;
            Long tripperId = 1L;
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> tripLikeService.addLike(tripId, tripperId)).isInstanceOf(TripNotFoundException.class);

        }
    }

    @Nested
    class 좋아요_취소_테스트{

        @Test
        void 성공시_메서드_호출_테스트 (){

            // given
            Long tripId = 1L;
            Long tripperId = 1L;
            Trip trip = TripFixture.undecided_Id(tripId, tripperId);
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
            given(likeRepository.findByTripIdAndTripperId(eq(tripId), eq(tripperId))).willReturn(Optional.of(Like.of(tripId, tripperId)));

            // when
            tripLikeService.removeLike(tripId, tripperId);

            // then
            verify(likeRepository).delete(any(Like.class));
        }

        @Test
        void 존재하지_않는_여행일_경우_TripNotFoundException_예외가_발생한다(){
            // given
            Long tripId = 1L;
            Long tripperId = 1L;
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tripLikeService.removeLike(tripId, tripperId)).isInstanceOf(TripNotFoundException.class);
        }

        @Test
        void 좋아요_하지_않았던_여행에_대해_좋아요_취소_요청을_하면_LikeNotFoundException_예외가_발생한다(){
            // given
            Long tripId = 1L;
            Long tripperId = 1L;
            Trip trip = TripFixture.undecided_Id(tripId, tripperId);
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
            given(likeRepository.findByTripIdAndTripperId(eq(tripId), eq(tripperId))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tripLikeService.removeLike(tripId, tripperId)).isInstanceOf(LikeNotFoundException.class);
        }
    }

}
