package com.cosain.trilo.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@EqualsAndHashCode(of = {"value"})
@ToString(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ScheduleContent {

    @Column(name = "content")
    private String value;

    public static ScheduleContent of(String rawContent) {
        // 추후 검증이 필요한 부분이 있으면 여기에
        return new ScheduleContent(rawContent);
    }

    private ScheduleContent(String value) {
        this.value = value;
    }
}
