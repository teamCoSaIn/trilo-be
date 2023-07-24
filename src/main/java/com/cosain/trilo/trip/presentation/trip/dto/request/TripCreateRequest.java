package com.cosain.trilo.trip.presentation.trip.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여행 생성을 위한 정보를 이 객체에 바인딩합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripCreateRequest {

    /**
     * 생성할 여행의 제목
     */
    private String title;

    /**
     * 여행생성요청을 생성합니다.
     * @param title : 생성할 여행의 제목
     */
    public TripCreateRequest(String title) {
        this.title = title;
    }

}
