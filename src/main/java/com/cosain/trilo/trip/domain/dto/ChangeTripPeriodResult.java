package com.cosain.trilo.trip.domain.dto;

import com.cosain.trilo.trip.domain.entity.Day;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 기간 수정의 결과입니다.
 */
@Getter
public class ChangeTripPeriodResult {

    /**
     * 삭제되는 Day의 Id들 (기간 변경으로 인해 삭제해야 할 Day의 Id들)
     */
    private final List<Long> deletedDayIds = new ArrayList<>();

    /**
     * 생성되는 Day들 (기간 변경으로 새로 생겨난 Day들 -> 저장 필요)
     */
    private final List<Day> createdDays = new ArrayList<>();

    /**
     * 삭제되는 Day들과, 생성되는 Day들을 파라미터로 받아 여행 기간 수정 결과를 바인딩한 객체를 만듭니다.
     * @param deletedDays 삭제되는 Day들
     * @param createdDays 새로 생겨난 Day들
     * @return 여행기간 수정 결과
     */
    public static ChangeTripPeriodResult of(List<Day> deletedDays, List<Day> createdDays) {
        List<Long> deleteDayIds = deletedDays.stream()
                .map(Day::getId)
                .toList(); // Day의 Id들만 리스트로 추출

        return new ChangeTripPeriodResult(deleteDayIds, createdDays);
    }

    private ChangeTripPeriodResult(List<Long> deletedDayIds, List<Day> createdDays) {
        this.deletedDayIds.addAll(deletedDayIds);
        this.createdDays.addAll(createdDays);
    }
}
