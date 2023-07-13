package com.cosain.trilo.unit.user.domain;

import com.cosain.trilo.user.NickName;
import com.cosain.trilo.user.domain.exception.InvalidNickNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NickNameTest {

    @DisplayName("닉네임이 null 이 아닌 적정 길이의 문자열 -> 정상 생성")
    @ValueSource(strings = {"닉네임", "꽃", "     "})
    @ParameterizedTest
    void successCreateTest(String rawNickName) {
        // given : rawNickName

        // when
        NickName nickName = NickName.of(rawNickName);

        // then
        assertThat(nickName.getValue()).isEqualTo(rawNickName);
    }

    @DisplayName("닉네임이 null -> InvalidNickNameException")
    @Test
    void nullNickNameTest() {
        // given
        String rawNickName = null;

        // when & then
        assertThatThrownBy(() -> NickName.of(rawNickName))
                .isInstanceOf(InvalidNickNameException.class);
    }

    @DisplayName("100 자 넘어가는 닉네임 -> InvalidNickNameException")
    @Test
    void tooLongNickNameTest() {
        // given
        String longRawNickName = "가".repeat(101);

        // when & then
        assertThatThrownBy(() -> NickName.of(longRawNickName))
                .isInstanceOf(InvalidNickNameException.class);
    }

    @Test
    @DisplayName("닉네임의 문자열이 같으면 동등하다.")
    void testEquality() {
        // given
        String rawNickName1 = "닉네임";
        String rawNickName2 = "닉네임";

        // when
        NickName nickName1 = NickName.of(rawNickName1);
        NickName nickName2 = NickName.of(rawNickName2);

        // then
        assertThat(nickName1).isEqualTo(nickName2);
    }

}
