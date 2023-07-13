package com.cosain.trilo.user;

import com.cosain.trilo.user.domain.exception.InvalidNickNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@ToString(of = {"value"})
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class NickName {

    @Column(name = "nick_name")
    private String value;

    private static int MIN_LENGTH = 1;
    private static int MAX_LENGTH = 100;

    public static NickName of(String rawNickName) {
        validate(rawNickName);
        return new NickName(rawNickName);
    }

    private static void validate(String rawNickName) {
        if (rawNickName == null || rawNickName.length() < MIN_LENGTH || rawNickName.length() > MAX_LENGTH) {
            throw new InvalidNickNameException("사용자 닉네임은 null일 수 없고 1자 이상 100자 이하여야 함");
        }
    }

    private NickName(String value) {
        this.value = value;
    }
}
