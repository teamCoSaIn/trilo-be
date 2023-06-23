package com.cosain.trilo.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidScheduleContentException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@EqualsAndHashCode(of = {"value"})
@ToString(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleContent {

    private static final ScheduleContent DEFAULT_CONTENT = ScheduleContent.of("");

    @Column(name = "content")
    private String value;

    public static ScheduleContent of(String rawContent) {
        if (rawContent == null) {
            throw new InvalidScheduleContentException("일정의 본문이 null");
        }
        return new ScheduleContent(rawContent);
    }

    public static ScheduleContent defaultContent() {
        return DEFAULT_CONTENT;
    }

    private ScheduleContent(String value) {
        this.value = value;
    }
}
