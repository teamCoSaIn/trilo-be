
package com.cosain.trilo.unit.trip.infra.dao;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.application.day.service.day_search.ScheduleSummary;
import com.cosain.trilo.trip.application.schedule.service.schedule_detail_search.ScheduleDetail;
import com.cosain.trilo.trip.application.trip.service.temporary_search.TempScheduleListQueryParam;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.infra.dao.ScheduleQueryDAOImpl;
import com.cosain.trilo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RepositoryTest
@DisplayName("ScheduleQueryDAOImpl 테스트")
public class ScheduleQueryDAOImplTest {

    @Autowired
    private ScheduleQueryDAOImpl scheduleQueryDAOImpl;

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
        ScheduleDetail dto = scheduleQueryDAOImpl.findScheduleDetailById(schedule.getId()).get();

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

        //TODO: cursor ScheduleId 없을 때에 대한 테스트 필요

        @Test
        @DirtiesContext
        @DisplayName("커서가 가리키는 일정 이후의 일정들이 size 만큼, ScheduleIndex에 오름차순으로 조회된다.")
        void find_With_CursorTest(){
            // given
            Long tripperId = setupTripperId();
            Trip trip = setupUndecidedTripAndPersist(tripperId);

            Schedule schedule1 = setupTemporaryScheduleAndPersist(trip, 10000L);
            Schedule schedule2 = setupTemporaryScheduleAndPersist(trip, 50000L);
            Schedule schedule3 = setupTemporaryScheduleAndPersist(trip, 40000L);
            Schedule schedule4 = setupTemporaryScheduleAndPersist(trip, 30000L);
            Schedule schedule5 = setupTemporaryScheduleAndPersist(trip, 20000L);
            em.flush();
            em.clear();

            Long tripId = trip.getId();
            Long cursorScheduleId = schedule1.getId();
            int pageSize = 3;

            var queryParam = TempScheduleListQueryParam.of(tripId, cursorScheduleId, pageSize);

            // when
            var result = scheduleQueryDAOImpl.findTemporarySchedules(queryParam);

            // then
            assertTrue(result.isHasNext());
            assertThat(result.getTempSchedules().size()).isEqualTo(3);
            assertThat(result.getTempSchedules()).map(ScheduleSummary::getScheduleId)
                    .containsExactly(schedule5.getId(), schedule4.getId(), schedule3.getId());
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
        assertTrue(scheduleQueryDAOImpl.existById(scheduleId));
        assertFalse(scheduleQueryDAOImpl.existById(notExistScheduleId));
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
