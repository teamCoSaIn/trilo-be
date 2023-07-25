package com.cosain.trilo.trip.domain.vo;

/**
 * <p>여행의 상태를 정의한 enum입니다. 여행은 다음 3가지 상태 중 하나를 가집니다.</p>
 * <ul>
 *     <li>{@link TripStatus#UNDECIDED}</li>
 *     <li>{@link TripStatus#DECIDED}</li>
 *     <li>{@link TripStatus#FINISHED}</li>
 * </ul>
 */
public enum TripStatus {

    /**
     * 여행의 기간이 정해지지 않은 경우
     */
    UNDECIDED,

    /**
     * 여행의 기간이 정해진 경우
     */
    DECIDED,

    /**
     * 여행이 완료된 경우
     */
    FINISHED
}
