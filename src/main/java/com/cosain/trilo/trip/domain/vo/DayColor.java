package com.cosain.trilo.trip.domain.vo;


import com.cosain.trilo.trip.domain.exception.InvalidDayColorNameException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum DayColor {

    RED(0, "#FB6C6C"),
    ORANGE(1, "#F4A17D"),
    LIGHT_GREEN(2, "#B9F15D"),
    GREEN(3, "#43D65A"),
    BLUE(4, "#4D77FF"),
    PURPLE(5, "#D96FF8"),
    VIOLET(6, "#8F57FB"),
    BLACK(7, "#383B40");

    private static final Map<Integer, DayColor> DAY_COLORS = Arrays
            .stream(values())
            .collect(Collectors.toUnmodifiableMap(DayColor::getId, Function.identity()));

    private static final int COLOR_COUNT = DAY_COLORS.size();

    private final int id;
    private final String value;

    DayColor(int id, String value) {
        this.value = value;
        this.id = id;
    }

    public static DayColor of(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new InvalidDayColorNameException("색상 이름이 null 이거나 지원되지 않는 이름임", e);
        }
    }

    public static DayColor random(Random random) {
        int randomDayColorId = random.nextInt(COLOR_COUNT);
        return DAY_COLORS.get(randomDayColorId);
    }

    @Override
    public String toString() {
        return String.format(
                "%s(name=%s, value=%s)",
                this.getClass().getSimpleName(), name(), value);
    }

}
