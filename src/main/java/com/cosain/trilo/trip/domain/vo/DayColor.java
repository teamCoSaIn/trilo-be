package com.cosain.trilo.trip.domain.vo;


import com.cosain.trilo.trip.domain.exception.InvalidDayColorNameException;
import lombok.Getter;

@Getter
public enum DayColor {

    RED("#FB6C6C"),
    ORANGE("#F4A17D"),
    LIGHT_GREEN("#B9F15D"),
    GREEN("#43D65A"),
    BLUE("#4D77FF"),
    PURPLE("#D96FF8"),
    VIOLET("#8F57FB"),
    BLACK("#383B40");

    private final String value;

    DayColor(String value) {
        this.value = value;
    }

    public static DayColor of(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new InvalidDayColorNameException("색상 이름이 null 이거나 지원되지 않는 이름임", e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s(name=%s, value=%s)",
                this.getClass().getSimpleName(), name(), value);
    }

}
