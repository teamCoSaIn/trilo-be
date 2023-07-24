package com.cosain.trilo.trip.application.trip.service.trip_create;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 여행 생성에 필요한 명령(command, 비즈니스 입력 모델)입니다.
 * @see TripCreateService
 */
@Getter
@EqualsAndHashCode(of = {"tripperId", "tripTitle"})
public class TripCreateCommand {

    /**
     * 여행 생성을 시도하는 여행자(사용자) 식별자
     */
    private final long tripperId;

    /**
     * 생성하고자 하는 여행의 제목
     * @see TripTitle
     */
    private final TripTitle tripTitle;

    /**
     * 여행 생성 명령(비즈니스 입력 모델)을 생성합니다.
     * @param tripperId : 여행 생성을 시도하는 여행자(사용자) 식별자)
     * @param rawTitle : 생성하고자 하는 여행의 제목
     * @return 여행생성명령
     * @throws CustomValidationException : 명령 생성과정에서 발생한 예외들을 묶은 예외
     */
    public static TripCreateCommand of(Long tripperId, String rawTitle) throws CustomValidationException {
        List<CustomException> exceptions = new ArrayList<>(); // 발생 예외를 수집할 예외 수집기

        TripTitle tripTitle = makeTripTitleAndValidate(rawTitle, exceptions); // 여행 제목 생성 및 비즈니스 입력 검증

        if (!exceptions.isEmpty()) {
            // 입력 검증 과정에서 예외가 하나라도 발생할 경우 이들을 모아서, 검증 예외를 발생시킴.
            throw new CustomValidationException(exceptions);
        }
        return new TripCreateCommand(tripperId, tripTitle);
    }

    private TripCreateCommand(long tripperId, TripTitle tripTitle) {
        this.tripperId = tripperId;
        this.tripTitle = tripTitle;
    }

    /**
     * <p>여행제목({@link TripTitle})을 생성하고, 이 과정에서 여행 제목에 대한 입력 검증을 수행합니다.</p>
     * <p>여행제목 생성 과정에서 예외가 발생하면 예외 수집기에 비즈니스 예외를 수집합니다.</p>
     * @param rawTitle : 여행의 제목
     * @param exceptions : 검증 과정에서 발생한 예외를 수집할 컬렉션
     * @return 정상적인 입력이면 여행 제목 객체를, 그렇지 않을 경우 null 반환
     * @see TripTitle
     */
    private static TripTitle makeTripTitleAndValidate(String rawTitle, List<CustomException> exceptions) {
        try {
            return TripTitle.of(rawTitle);
        } catch (CustomException e) {
            // 비즈니스 예외가 발생할 경우 예외 수집
            exceptions.add(e);
        }
        return null;
    }
}
