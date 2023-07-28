package com.cosain.trilo.trip.presentation.trip.dto.response;

import lombok.Getter;

/**
 * 여행 기간수정 후, 응답을 바인딩할 객체입니다.
 * <p>기간수정된 여행에 대한 정보가 바인딩됩니다.</p>
 */
@Getter
public class TripPeriodUpdateResponse {

    /**
     * 여행의 식별자(id)
     */
    private final Long tripId;

    /**
     * 여행 기간수정 응답을 생성합니다.
     * @param tripId 기간수정된 여행의 식별자(id)
     */
    public TripPeriodUpdateResponse(Long tripId) {
        this.tripId = tripId;
    }
}
