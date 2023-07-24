package com.cosain.trilo.trip.presentation.trip.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여행 생성 후, 응답을 바인딩할 객체입니다.
 * <p>생성된 여행에 대한 정보가 바인딩됩니다.</p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripCreateResponse {

    /**
     * 생성된 여행의 식별자(id)
     */
    private Long tripId;

    /**
     * 여행생성응답을 생성합니다.
     * @param tripId : 생성된 여행의 식별자(id)
     * @return 여행생성응답
     */
    public static TripCreateResponse from(Long tripId) {
        return new TripCreateResponse(tripId);
    }
    private TripCreateResponse(Long tripId) {
        this.tripId = tripId;
    }

}
