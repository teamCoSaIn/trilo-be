package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleContentException;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ScheduleContent(일정 본문) 테스트")
public class ScheduleContentTest {

    @DisplayName("일정 본문이 null 이 아닌 적정 길이의 문자열(공백 허용) -> 정상 생성")
    @ValueSource(strings = {"일정 본문", "", "     "})
    @ParameterizedTest
    void successCreateTest(String rawContent) {
        // given : rawTitle

        // when
        ScheduleContent scheduleContent = ScheduleContent.of(rawContent);

        // then
        assertThat(scheduleContent).isEqualTo(ScheduleContent.of(rawContent));
    }

    @DisplayName("일정 본문이 null -> InvalidScheduleContentException")
    @Test
    void nullContentTest() {
        // given
        String rawContent = null;

        // when & then
        assertThatThrownBy(() -> ScheduleContent.of(rawContent))
                .isInstanceOf(InvalidScheduleContentException.class);
    }

    @DisplayName("일정 본문 바이트 크기 65535 -> 정상 생성")
    @Test
    public void maxContentTest() {
        // given
        byte[] bytes = new byte[65535];
        Arrays.fill(bytes, (byte) 'A');
        String rawContent = new String(bytes, StandardCharsets.UTF_8);

        // when
        ScheduleContent scheduleContent = ScheduleContent.of(rawContent);

        // then
        assertThat(scheduleContent.getValue()).isEqualTo(rawContent);
    }

    @DisplayName("일정 본문 바이트 크기 65535 초과 -> InvalidScheduleContentException")
    @ParameterizedTest
    @ValueSource(ints = {65536, 65537, 12345678})
    public void largeContentText(int size) {
        // given
        byte[] bytes = new byte[size];
        Arrays.fill(bytes, (byte) 'A');
        String rawContent = new String(bytes, StandardCharsets.UTF_8);

        // when && then
        assertThatThrownBy(()-> ScheduleContent.of(rawContent))
                .isInstanceOf(InvalidScheduleContentException.class);
    }

    @Test
    @DisplayName("디폴트 ScheduleContent는 빈 문자열이다.")
    void testDefault() {
        // when
        ScheduleContent scheduleContent = ScheduleContent.defaultContent();

        // then
        assertThat(scheduleContent.getValue()).isEqualTo("");
    }

    @Test
    @DisplayName("본문 내용이 같으면 동등하다.")
    void testEquality() {
        // given
        String rawContent1 = "일정본문";
        String rawContent2 = "일정본문";

        // when
        ScheduleContent scheduleContent1 = ScheduleContent.of(rawContent1);
        ScheduleContent scheduleContent2 = ScheduleContent.of(rawContent2);

        // then
        assertThat(scheduleContent1).isEqualTo(scheduleContent2);
    }

}
