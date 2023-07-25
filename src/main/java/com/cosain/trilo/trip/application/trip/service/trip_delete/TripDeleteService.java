package com.cosain.trilo.trip.application.trip.service.trip_delete;

import com.cosain.trilo.common.exception.trip.NoTripDeleteAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 여행 삭제를 수행하는 애플리케이션 서비스입니다.
 */
@RequiredArgsConstructor
@Service
public class TripDeleteService {

    /**
     * 여행을 조회, 삭제할 리포지토리
     */
    private final TripRepository tripRepository;

    /**
     * 여행이 가진 Day 들을 삭제할 리포지토리
     */
    private final DayRepository dayRepository;

    /**
     * 여행이 가진 일정들을 삭제할 리포지토리
     */
    private final ScheduleRepository scheduleRepository;

    /**
     * Trip 및 Trip이 가진 Day, Schedule들을 모두 삭제합니다.
     * @param tripId 삭제할 여행 id(식별자)
     * @param requestTripperId 여행 삭제를 시도하는 여행자(사용자)
     * @throws TripNotFoundException 일치하는 식별자의 여행을 찾지 못 했을 때
     * @throws NoTripDeleteAuthorityException 여행을 삭제할 권한이 없을 때
     */
    @Transactional
    public void deleteTrip(Long tripId, Long requestTripperId) throws TripNotFoundException, NoTripDeleteAuthorityException {
        Trip trip = findTrip(tripId); // 여행 조회 -> 일치하는 여행 없으면 예외 발생
        validateTripDeleteAuthority(trip, requestTripperId); // 삭제 권한 검증 -> 권한 없으면 예외 발생

        // 여기서부터 실제로 삭제 (일정-> Day -> 여행 순으로 삭제해야함)
        scheduleRepository.deleteAllByTripId(tripId);
        dayRepository.deleteAllByTripId(tripId);
        tripRepository.delete(trip);
    }

    /**
     * 삭제할 여행을 리포지토리에서 찾아옵니다.
     * @param tripId 삭제할 여행 id(식별자)
     * @return 여행
     * @throws TripNotFoundException 일치하는 식별자의 여행을 찾지 못 했을 때
     */
    private Trip findTrip(Long tripId) throws TripNotFoundException{
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("여행 삭제 시도 -> 일치하는 식별자의 여행을 찾지 못 함"));
    }

    /**
     * 여행 삭제를 요청한 사용자(여행자)가 여행을 삭제할 권한이 있는 지 검증합니다.
     * @param trip 여행
     * @param requestTripperId 여행 삭제를 시도하는 여행자(사용자)
     * @throws NoTripDeleteAuthorityException 여행을 삭제할 권한이 없을 때
     */
    private void validateTripDeleteAuthority(Trip trip, Long requestTripperId) throws NoTripDeleteAuthorityException {
        if (!trip.getTripperId().equals(requestTripperId)) {
            throw new NoTripDeleteAuthorityException("여행 삭제 시도 -> 삭제할 권한 없음");
        }
    }
}
