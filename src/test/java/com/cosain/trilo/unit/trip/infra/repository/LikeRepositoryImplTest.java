package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Like;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.infra.repository.LikeRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LikeRepositoryImpl 테스트")
public class LikeRepositoryImplTest extends RepositoryTest {

    @Autowired
    private LikeRepositoryImpl likeRepositoryImpl;

    @Test
    @DisplayName("delete 테스트")
    void deleteTest() {
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTrip(tripperId);
        Long tripId = trip.getId();
        Like like = Like.of(tripId, tripperId);
        likeRepositoryImpl.save(like);
        flushAndClear();

        // when
        likeRepositoryImpl.delete(like);
        flushAndClear();

        // then
        assertThat(likeRepositoryImpl.existsByTripIdAndTripperId(tripId, tripperId)).isFalse();
    }

    @Test
    @DisplayName("existsByTripIdAndTripperId 테스트")
    void existsByTripIdAndTripperIdTest(){
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTrip(tripperId);
        Long tripId = trip.getId();
        Like like = Like.of(tripId, tripperId);
        likeRepositoryImpl.save(like);
        flushAndClear();

        // when & then
        assertThat(likeRepositoryImpl.existsByTripIdAndTripperId(tripId, tripperId)).isTrue();
    }

    @Test
    @DisplayName("findByTripIdAndTripperId 테스트")
    void findByTripIdAndTripperIdTest(){
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTrip(tripperId);
        Long tripId = trip.getId();
        Like like = Like.of(tripId, tripperId);
        likeRepositoryImpl.save(like);
        flushAndClear();

        // when
        Optional<Like> likeOptional = likeRepositoryImpl.findByTripIdAndTripperId(tripId, tripperId);

        // then
        assertThat(likeOptional.isPresent()).isTrue();
        assertThat(likeOptional.get()).isEqualTo(like);

    }

}
