package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleContentException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.nio.charset.StandardCharsets;


@Getter
@EqualsAndHashCode(of = {"value"})
@ToString(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleContent {

    private static final ScheduleContent DEFAULT_CONTENT = ScheduleContent.of("");
    private static final int MAX_BYTE = 65535;

    @Column(name = "schedule_content")
    private String value;

    public static ScheduleContent of(String rawContent) {
        validateContent(rawContent);
        return new ScheduleContent(rawContent);
    }

    private static void validateContent(String rawContent) {
        if (rawContent == null) {
            throw new InvalidScheduleContentException("일정의 본문이 null");
        }
        int textSize = rawContent.getBytes(StandardCharsets.UTF_8).length;
        if (textSize > MAX_BYTE) {
            throw new InvalidScheduleContentException("일정의 본문 크기가 제한보다 큼");
        }
    }

    public static ScheduleContent defaultContent() {
        return DEFAULT_CONTENT;
    }

    private ScheduleContent(String value) {
        this.value = value;
    }
}
