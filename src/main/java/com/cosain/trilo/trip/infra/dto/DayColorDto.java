package com.cosain.trilo.trip.infra.dto;

import lombok.Getter;

@Getter
public class DayColorDto {
    private String name;
    private String code;

    private DayColorDto(String name, String code){
        this.name = name;
        this.code = code;
    }

    public static DayColorDto of(String name, String code){
        return new DayColorDto(name, code);
    }
}
