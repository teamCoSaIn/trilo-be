package com.cosain.trilo.unit.trip.command.domain.vo;

import com.cosain.trilo.trip.command.domain.exception.ScheduleIndexRangeException;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[TripCommand] ScheduleIndexTest")
public class ScheduleIndexTest {

    @Nested
    @DisplayName("생성 테스트")
    class createTest {

        @ParameterizedTest
        @DisplayName("최댓값보다 큰 인덱스 생성 -> ScheduleIndexRangeException")
        @ValueSource(longs = {
                5_000_000_000_000_000_001L,
                5_000_000_000_000_000_002L,
                6_000_000_000_000_000_000L,
                7_000_000_000_000_000_000L,
                8_888_888_888_888_888_888L,
                Long.MAX_VALUE})
        public void when_create_with_too_big_index_then_it_throws_ScheduleIndexRangeException(long indexValue) {
            assertThatThrownBy(() -> ScheduleIndex.of(indexValue))
                    .isInstanceOf(ScheduleIndexRangeException.class);
        }

        @ParameterizedTest
        @DisplayName("최솟값보다 작은 인덱스 생성 -> ScheduleIndexRangeException")
        @ValueSource(longs = {
                -5_000_000_000_000_000_001L,
                -5_000_000_000_000_000_002L,
                -6_000_000_000_000_000_000L,
                -7_000_000_000_000_000_000L,
                -8_888_888_888_888_888_888L,
                Long.MIN_VALUE})
        public void when_create_with_too_small_index_then_it_throws_ScheduleIndexRangeException(long indexValue) {
            assertThatThrownBy(() -> ScheduleIndex.of(indexValue))
                    .isInstanceOf(ScheduleIndexRangeException.class);
        }

    }

    @Nested
    @DisplayName("generateNextIndex 테스트")
    class generateNextIndexTest {

        @Test
        @DisplayName("다음에 오는 순서가 범위를 벗어나는 경우 예외 발생")
        public void when_nextIndex_is_too_big_index_then_it_throws_ScheduleIndexRangeException() {
            ScheduleIndex index = ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE);

            assertThatThrownBy(index::generateNextIndex)
                    .isInstanceOf(ScheduleIndexRangeException.class);
        }


        @Test
        @DisplayName("최댓값을 넘지않는 인덱스의 다음 인덱스 생성 시 정상적으로 인덱스 생성")
        public void successfully_generate_nextIndex() {
            ScheduleIndex index = ScheduleIndex.ZERO_INDEX;

            ScheduleIndex nextIndex = index.generateNextIndex();
            assertThat(nextIndex.getValue()).isEqualTo(index.getValue() + ScheduleIndex.DEFAULT_SEQUENCE_GAP);
        }

    }

}
