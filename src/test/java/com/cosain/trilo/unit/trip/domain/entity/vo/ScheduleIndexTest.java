package com.cosain.trilo.unit.trip.domain.entity.vo;

import com.cosain.trilo.trip.domain.exception.ScheduleIndexRangeException;
import com.cosain.trilo.trip.domain.entity.vo.ScheduleIndex;
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

    @Nested
    @DisplayName("generateBeforeIndex 테스트")
    class generateBeforeIndexTest {

        @Test
        @DisplayName("생성되는 순서가 범위를 벗어나는 경우 예외 발생")
        public void when_generatedIndex_is_too_small_index_then_it_throws_ScheduleIndexRangeException() {
            ScheduleIndex index = ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE);

            assertThatThrownBy(index::generateBeforeIndex)
                    .isInstanceOf(ScheduleIndexRangeException.class);
        }


        @Test
        @DisplayName("다음에 오는 인덱스가 최솟값의 범위보다 크거나 같을 경우 정상적으로 인덱스 생성")
        public void successfully_generate_nextIndex() {
            ScheduleIndex index = ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE + ScheduleIndex.DEFAULT_SEQUENCE_GAP);

            ScheduleIndex generatedIndex = index.generateBeforeIndex();
            assertThat(generatedIndex.getValue()).isEqualTo(ScheduleIndex.MIN_INDEX_VALUE);
        }

    }

    @Nested
    @DisplayName("Mid 메서드를 통해 중간 인덱스 생성되는 지 테스트")
    class MidTest {

        @Test
        @DisplayName("1, 5 -> 3")
        public void one_and_five_returns_three() {
            // given
            ScheduleIndex index1 = ScheduleIndex.of(1);
            ScheduleIndex index2 = ScheduleIndex.of(5);

            // when
            ScheduleIndex midIndex = index1.mid(index2);

            // then
            assertThat(midIndex).isEqualTo(ScheduleIndex.of(3));
        }

        @Test
        @DisplayName("(최대 인덱스 -2) , (최대 인덱스) -> (최댓인덱스 -1)")
        public void max_minus_two_and_max_returns_max_minus_one() {
            // given
            ScheduleIndex index1 = ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE - 2);
            ScheduleIndex index2 = ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE);

            // when
            ScheduleIndex midIndex = index1.mid(index2);

            // then
            assertThat(midIndex).isEqualTo(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE - 1));
        }

        @Test
        @DisplayName("(최소 인덱스) , (최대 인덱스) -> 0")
        public void max_and_min_returns_zero() {
            // given
            ScheduleIndex index1 = ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE);
            ScheduleIndex index2 = ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE);

            // when
            ScheduleIndex midIndex = index1.mid(index2);

            // then
            assertThat(midIndex).isEqualTo(ScheduleIndex.ZERO_INDEX);
        }

        @Test
        @DisplayName("3, 4 -> 3")
        public void three_and_four_returns_three() {
            // given
            ScheduleIndex index1 = ScheduleIndex.of(3);
            ScheduleIndex index2 = ScheduleIndex.of(4);

            // when
            ScheduleIndex midIndex = index1.mid(index2);

            // then
            assertThat(midIndex).isEqualTo(ScheduleIndex.of(3));
        }

    }

}
