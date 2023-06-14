package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidDayColorNameException;
import com.cosain.trilo.trip.domain.vo.DayColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DayColor 테스트")
public class DayColorTest {

    @Nested
    @DisplayName("색상 이름으로 DayColor를 생성할 때")
    class OfTest {

        @DisplayName("색상 이름이 null -> InvalidDayColorNameException 발생")
        @Test
        public void testNullName() {
            // given
            String name = null;

            // when & then
            assertThatThrownBy(() -> DayColor.of(name))
                    .isInstanceOf(InvalidDayColorNameException.class);
        }

        @ValueSource(strings = {"adfadf", "rainbow", "pink", "", "999", "GOLD"})
        @ParameterizedTest
        @DisplayName("유효하지 않은 색상 이름 -> InvalidDayColorNameException 발생")
        public void testInvalidDayColorName(String name) {
            // given : name

            // when & then
            assertThatThrownBy(() -> DayColor.of(name))
                    .isInstanceOf(InvalidDayColorNameException.class);
        }

        @ValueSource(strings = {"red", "RED"})
        @ParameterizedTest
        @DisplayName("색상 이름이 RED -> RED DayColor 생성")
        public void testRed(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.RED);
            assertThat(dayColor.getValue()).isEqualTo("#FB6C6C");
        }

        @ValueSource(strings = {"orange", "ORANGE"})
        @ParameterizedTest
        @DisplayName("색상 이름이 ORANGE -> ORANGE DayColor 생성")
        public void testOrange(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.ORANGE);
            assertThat(dayColor.getValue()).isEqualTo("#F4A17D");
        }

        @ValueSource(strings = {"LIGHT_GREEN", "light_green"})
        @ParameterizedTest
        @DisplayName("색상 이름이 LIGHT_GREEN -> LIGHT_GREEN DayColor 생성")
        public void testLightGreen(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.LIGHT_GREEN);
            assertThat(dayColor.getValue()).isEqualTo("#B9F15D");
        }

        @ValueSource(strings = {"GREEN", "green"})
        @ParameterizedTest
        @DisplayName("색상 이름이 GREEN -> GREEN DayColor 생성")
        public void testGreen(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.GREEN);
            assertThat(dayColor.getValue()).isEqualTo("#43D65A");
        }

        @ValueSource(strings = {"BLUE", "blue"})
        @ParameterizedTest
        @DisplayName("색상 이름이 BLUE -> BLUE DayColor 생성")
        public void testBlue(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.BLUE);
            assertThat(dayColor.getValue()).isEqualTo("#4D77FF");
        }

        @ValueSource(strings = {"PURPLE", "purple"})
        @ParameterizedTest
        @DisplayName("색상 이름이 PURPLE -> PURPLE DayColor 생성")
        public void testPurple(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.PURPLE);
            assertThat(dayColor.getValue()).isEqualTo("#D96FF8");
        }

        @ValueSource(strings = {"VIOLET", "violet"})
        @ParameterizedTest
        @DisplayName("색상 이름이 VIOLET -> VIOLET DayColor 생성")
        public void testViolet(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.VIOLET);
            assertThat(dayColor.getValue()).isEqualTo("#8F57FB");
        }

        @ValueSource(strings = {"BLACK", "black"})
        @ParameterizedTest
        @DisplayName("색상 이름이 BLACK -> BLACK DayColor 생성")
        public void testBlack(String name) {
            // given : name

            // when
            DayColor dayColor = DayColor.of(name);

            // then
            assertThat(dayColor).isSameAs(DayColor.BLACK);
            assertThat(dayColor.getValue()).isEqualTo("#383B40");
        }
    }


    @Nested
    @DisplayName("random 메서드를 통해 랜덤한 DayColor를 얻어올 때")
    public class RandomTest {

        // 항상 해당 값을 반환하는 Random
        private Random stubRandom(int returnValue) {
            return new Random() {
                @Override
                public int nextInt(int bound) {
                    return returnValue;
                }
            };
        }
        @Test
        @DisplayName("Random이 0 발생 -> RED DayColor 반환")
        public void testRed() {
            // given
            Random random = stubRandom(0);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.RED);
            assertThat(dayColor.getValue()).isEqualTo("#FB6C6C");
        }

        @Test
        @DisplayName("Random이 1 발생 -> ORANGE DayColor 반환")
        public void testOrange() {
            // given
            Random random = stubRandom(1);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.ORANGE);
            assertThat(dayColor.getValue()).isEqualTo("#F4A17D");
        }

        @Test
        @DisplayName("Random이 2 발생 -> LIGHT_GREEN DayColor 반환")
        public void testLightGreen() {
            // given
            Random random = stubRandom(2);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.LIGHT_GREEN);
            assertThat(dayColor.getValue()).isEqualTo("#B9F15D");
        }

        @Test
        @DisplayName("Random이 3 발생 -> GREEN DayColor 반환")
        public void testGreen() {
            // given
            Random random = stubRandom(3);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.GREEN);
            assertThat(dayColor.getValue()).isEqualTo("#43D65A");
        }

        @Test
        @DisplayName("Random이 4 발생 -> BLUE DayColor 생성")
        public void testBlue() {
            // given
            Random random = stubRandom(4);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.BLUE);
            assertThat(dayColor.getValue()).isEqualTo("#4D77FF");
        }

        @Test
        @DisplayName("Random이 5 발생 -> PURPLE DayColor 생성")
        public void testPurple() {
            // given
            Random random = stubRandom(5);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.PURPLE);
            assertThat(dayColor.getValue()).isEqualTo("#D96FF8");
        }

        @Test
        @DisplayName("Random이 6 발생 -> VIOLET DayColor 생성")
        public void testViolet() {
            // given
            Random random = stubRandom(6);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.VIOLET);
            assertThat(dayColor.getValue()).isEqualTo("#8F57FB");
        }

        @Test
        @DisplayName("Random이 7 발생 -> BLACK DayColor 생성")
        public void testBlack() {
            // given
            Random random = stubRandom(7);

            // when
            DayColor dayColor = DayColor.random(random);

            // then
            assertThat(dayColor).isSameAs(DayColor.BLACK);
            assertThat(dayColor.getValue()).isEqualTo("#383B40");
        }
    }

}
