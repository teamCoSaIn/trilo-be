package com.cosain.trilo.trip.application.trip.service.trip_period_update;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 여행 기간수정에 필요한 명령(command, 비즈니스 입력 모델)입니다.
 */
@Getter
@EqualsAndHashCode(of = {"targetTripId", "requestTripperId", "tripPeriod"})
public class TripPeriodUpdateCommand {

    /**
     * 기간수정 할 여행의 식별자(id)
     */
    private final long targetTripId;

    /**
     * 여행 기간수정을 시도하는 여행자(사용자)의 식별자
     */
    private final long requestTripperId;

    /**
     * 수정할 여행의 기간
     */
    private final TripPeriod tripPeriod;

    /**
     * 여행 기간수정 명령(비즈니스 입력 모델)을 생성합니다.
     * @param targetTripId 대상이 되는 여행의 식별자
     * @param requestTripperId 여행 기간수정을 시도하는 여행자(사용자)의 식별자
     * @param startDate 여행의 시작일
     * @param endDate 여행의 종료일
     * @return 여행 기간수정 명령
     * @throws CustomValidationException 명령 생성과정에서 발생한 예외들을 묶은 예외
     */
    public static TripPeriodUpdateCommand of(
            long targetTripId, long requestTripperId, LocalDate startDate, LocalDate endDate)
            throws CustomValidationException {

        List<CustomException> exceptions = new ArrayList<>();// 발생 예외를 수집할 예외 수집기

        // 여행 기간 생성 및 비즈니스 입력 검증
        TripPeriod tripPeriod = makeTripPeriod(startDate, endDate, exceptions);

        if (!exceptions.isEmpty()) {
            // 입력 검증 과정에서 예외가 하나라도 발생할 경우 이들을 모아서, 검증 예외를 발생시킴.
            throw new CustomValidationException(exceptions);
        }
        return new TripPeriodUpdateCommand(targetTripId, requestTripperId, tripPeriod);
    }

    /**
     * <p>여행기간({@link TripPeriod})을 생성하고, 이 과정에서 여행 기간에 대한 입력 검증을 수행합니다.</p>
     * <p>여행기간 생성 과정에서 예외가 발생하면 예외 수집기에 비즈니스 예외를 수집합니다.</p>
     * @param startDate : 여행의 시작일
     * @param endDate : 여행의 종료일
     * @param exceptions : 검증 과정에서 발생한 예외를 수집할 컬렉션
     * @return 정상적인 입력이면 여행 기간 객체를, 그렇지 않을 경우 null 반환
     * @see TripPeriod
     */
    private static TripPeriod makeTripPeriod(LocalDate startDate, LocalDate endDate, List<CustomException> exceptions) {
        try {
            return TripPeriod.of(startDate, endDate);
        } catch (CustomException e) {
            // 비즈니스 예외가 발생할 경우 예외 수집
            exceptions.add(e);
        }
        return null;
    }

    private TripPeriodUpdateCommand(long targetTripId, long requestTripperId, TripPeriod tripPeriod) {
        this.targetTripId = targetTripId;
        this.requestTripperId = requestTripperId;
        this.tripPeriod = tripPeriod;
    }
}
