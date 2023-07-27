package com.cosain.trilo.trip.application.trip.service.trip_period_update;

import com.cosain.trilo.common.exception.trip.NoTripUpdateAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.common.exception.trip.EmptyPeriodUpdateException;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 여행 기간수정을 수행하는 애플리케이션 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class TripPeriodUpdateService {

    /**
     * 수정할 기간을 저장, 관리하고 있는 리포지토리
     */
    private final TripRepository tripRepository;

    /**
     * 여행이 가진 Day들을 저장, 관리하고 있는 리포지토리
     */
    private final DayRepository dayRepository;

    /**
     * 여행이 가진 일정들을 저장, 관리하고 있는 리포지토리
     */
    private final ScheduleRepository scheduleRepository;

    /**
     * 여행의 기간을 수정합니다.
     * @param command 여행 기간수정 명령(비즈니스 입력 모델)
     * @throws TripNotFoundException 여행을 찾을 수 없을 때
     * @throws NoTripUpdateAuthorityException 여행의 기간을 수정할 권한이 없을 때
     * @throws EmptyPeriodUpdateException 기간이 정해져있는데 빈 기간으로 수정하려고 할 때
     * @see TripPeriodUpdateCommand
     */
    @Transactional
    public void updateTripPeriod(TripPeriodUpdateCommand command)
            throws TripNotFoundException, NoTripUpdateAuthorityException, EmptyPeriodUpdateException {

        // 여행을 Day들과 조회 -> 여행 없으면 예외 발생
        Trip trip = findTripWithDays(command.getTargetTripId());

        // 여행의 기간을 수정할 권한이 있는 지 검증 -> 권한 없으면 예외 발생
        validateTripUpdateAuthority(trip, command.getRequestTripperId());

        // 여행 기간을 실제로 수정 -> 이 때 여행의 기간이 잡혀있는데 빈 기간으로 변경하려 하면 예외 발생
        changePeriod(trip, command.getTripPeriod());
    }

    /**
     * 여행을 여행이 가진 Day들과 함께 조회해옵니다.
     * @param targetTripId 기간수정의 대상이 되는 여행 Id
     * @return 여행
     * @throws TripNotFoundException 여행이 존재하지 않을 경우
     */
    private Trip findTripWithDays(Long targetTripId) throws TripNotFoundException {
        return tripRepository.findByIdWithDays(targetTripId)
                .orElseThrow(() -> new TripNotFoundException("일치하는 식별자의 Trip을 찾을 수 없음"));
    }

    /**
     * 여행의 기간을 수정할 권한이 있는 지 검증합니다.
     * @param trip 수정할 여행
     * @param requestTripperId 수정을 요청하는 사용자(여행자)
     * @throws NoTripUpdateAuthorityException 여행의 기간을 수정할 권한이 없을 때
     */
    private void validateTripUpdateAuthority(Trip trip, Long requestTripperId) throws NoTripUpdateAuthorityException {
        if (!trip.getTripperId().equals(requestTripperId)) {
            throw new NoTripUpdateAuthorityException("여행을 수정할 권한이 없는 사람이 수정하려고 시도함");
        }
    }

    /**
     * 여행의 기간을 실제로 수정합니다.
     * @param trip 수정할 여행
     * @param newPeriod 수정할 여행 기간
     * @throws EmptyPeriodUpdateException 기간이 정해져있는데 빈 기간으로 변경하려고 할 때
     */
    private void changePeriod(Trip trip, TripPeriod newPeriod) throws EmptyPeriodUpdateException {
        // 여행의 기간 수정 -> 기간 정해져있는데 빈 기간으로 변경할 때 예외 발생할 수 있음
        var changePeriodResult = trip.changePeriod(newPeriod);

        List<Day> createdDays = changePeriodResult.getCreatedDays();
        List<Long> deletedDayIds = changePeriodResult.getDeletedDayIds();

        if (!createdDays.isEmpty()) {
            // 새로 생성되는 Day가 하나라도 존재하면 Day들 일괄 저장
            dayRepository.saveAll(createdDays);
        }
        if (!deletedDayIds.isEmpty()) {
            // 삭제되는 Day가 하나라도 존재하면
            scheduleRepository.relocateDaySchedules(trip.getId(), null); // 해당 여행의 임시보관함 일정들 전체 재배치
            scheduleRepository.moveSchedulesToTemporaryStorage(trip.getId(), deletedDayIds); // 삭제되는 Day의 일정들을 모두 임시보관함 맨 뒤에 이동
            dayRepository.deleteAllByIds(deletedDayIds); // Day들 삭제
        }
    }

}
