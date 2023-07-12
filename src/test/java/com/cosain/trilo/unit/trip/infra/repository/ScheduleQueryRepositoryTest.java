
package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import com.cosain.trilo.trip.presentation.trip.dto.request.TempSchedulePageCondition;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RepositoryTest
@DisplayName("ScheduleQueryRepository 테스트")
public class ScheduleQueryRepositoryTest {

    @Autowired
    private ScheduleQueryRepository scheduleQueryRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findScheduleTest(){
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTripAndPersist(tripperId);

        Schedule schedule = setupTemporaryScheduleAndPersist(trip, 0L);
        em.flush();
        em.clear();

        // when
        ScheduleDetail dto = scheduleQueryRepository.findScheduleDetailById(schedule.getId()).get();

        // then
        assertThat(dto.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(dto.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(dto.getTitle()).isEqualTo(schedule.getScheduleTitle().getValue());
        assertThat(dto.getPlaceName()).isEqualTo(schedule.getPlace().getPlaceName());
        assertThat(dto.getCoordinate().getLatitude()).isEqualTo(schedule.getPlace().getCoordinate().getLatitude());
        assertThat(dto.getCoordinate().getLongitude()).isEqualTo(schedule.getPlace().getCoordinate().getLongitude());
        assertThat(dto.getOrder()).isEqualTo(schedule.getScheduleIndex().getValue());
        assertThat(dto.getContent()).isEqualTo(schedule.getScheduleContent().getValue());
    }

    @Nested
    @DisplayName("임시 보관함 조회 시")
    class findTemporaryScheduleListByTripIdTest{

        @Test
        @DirtiesContext
        @DisplayName("커서가 가리키는 일정 이후의 일정들이 size 만큼 조회된다.")
        void findTest(){
            // given
            Long tripperId = setupTripperId();
            Trip trip = setupUndecidedTripAndPersist(tripperId);

            Schedule schedule1 = setupTemporaryScheduleAndPersist(trip, 10000L);
            Schedule schedule2 = setupTemporaryScheduleAndPersist(trip, 20000L);
            Schedule schedule3 = setupTemporaryScheduleAndPersist(trip, 30000L);
            Schedule schedule4 = setupTemporaryScheduleAndPersist(trip, 40000L);
            em.flush();
            em.clear();

            Long tripId = trip.getId();
            Long cursorScheduleId = schedule1.getId();
            TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(cursorScheduleId);

            // when
            Slice<ScheduleSummary> scheduleSummaries = scheduleQueryRepository.findTemporaryScheduleListByTripId(tripId,tempSchedulePageCondition,PageRequest.ofSize(3));

            // then
            assertThat(scheduleSummaries.getSize()).isEqualTo(3);
        }

        @Test
        @DisplayName("scheduleIndex 기준 오름차순으로 조회된다.")
        void sortTest(){
            // given
            Long tripperId = setupTripperId();
            Trip trip = setupUndecidedTripAndPersist(tripperId);

            Schedule schedule1 = setupTemporaryScheduleAndPersist(trip, 10000L);
            Schedule schedule2 = setupTemporaryScheduleAndPersist(trip, 20000L);
            Schedule schedule3 = setupTemporaryScheduleAndPersist(trip, 30000L);
            em.flush();
            em.clear();

            Long tripId = trip.getId();
            Long cursorScheduleId = schedule1.getId();
            TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(cursorScheduleId);

            // when
            Slice<ScheduleSummary> scheduleSummaries = scheduleQueryRepository.findTemporaryScheduleListByTripId(tripId, tempSchedulePageCondition, PageRequest.ofSize(2));

            // then
            assertThat(scheduleSummaries.getSize()).isEqualTo(2);
            assertThat(scheduleSummaries)
                    .map(ScheduleSummary::getScheduleId)
                    .containsExactly(schedule2.getId(), schedule3.getId());
        }
    }

    @Test
    @DirtiesContext
    void existByIdTest(){
        // given
        Long tripperId = setupTripperId();
        Trip trip = setupUndecidedTripAndPersist(tripperId);
        Schedule schedule = setupTemporaryScheduleAndPersist(trip, 0L);
        em.flush();
        em.clear();

        long scheduleId = schedule.getId();
        long notExistScheduleId = scheduleId + 1; // 존재하지 않는 일정 식별자

        // when && then
        assertTrue(scheduleQueryRepository.existById(scheduleId));
        assertFalse(scheduleQueryRepository.existById(notExistScheduleId));
    }

    private Long setupTripperId() {
        User user = UserFixture.googleUser_NullId();
        em.persist(user);
        return user.getId();
    }

    private Trip setupUndecidedTripAndPersist(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);
        return trip;
    }

    private Schedule setupTemporaryScheduleAndPersist(Trip trip, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }

}
