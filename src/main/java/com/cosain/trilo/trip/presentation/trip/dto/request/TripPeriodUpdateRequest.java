package com.cosain.trilo.trip.presentation.trip.dto.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 여행 기간수정을 위한 정보를 이 객체에 바인딩합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPeriodUpdateRequest {

    /**
     * 여행의 시작일
     */
    private LocalDate startDate;

    /**
     * 여행의 종료일
     */
    private LocalDate endDate;

    /**
     * 여행 기간수정 요청을 생성합니다.
     * @param startDate 여행의 시작일
     * @param endDate 여행의 종료일
     */
    public TripPeriodUpdateRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
