package com.cosain.trilo.support;

import com.cosain.trilo.config.QueryDslConfig;
import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.fixture.UserFixture;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.infra.dao.TripQueryDAOImpl;
import com.cosain.trilo.trip.infra.repository.TripRepositoryImpl;
import com.cosain.trilo.user.domain.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@DirtiesContext
@Import({QueryDslConfig.class})
@ComponentScan(basePackageClasses = {TripQueryDAOImpl.class, TripRepositoryImpl.class})
public abstract class RepositoryTest {

    /**
     * 영속성 컨텍스트
     */
    @Autowired
    protected EntityManager em;

    protected void flushAndClear() {
        em.flush();
        em.clear();
    }


    /**
     * 저장소에 사용자를 저장하여 셋팅하고, 해당 사용자의 id를 발급받아 옵니다.
     * @return 새로 저장된 사용자의 id
     */
    protected Long setupTripperId() {
        User user = UserFixture.googleUser_NullId();
        em.persist(user);
        return user.getId();
    }

    /**
     * {@link TripStatus#UNDECIDED} 상태의 여행을 생성하고, 저장소에 저장하여 셋팅합니다.
     * @param tripperId 사용자(여행자)의 id
     * @return 여행
     */
    protected Trip setupUndecidedTrip(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);
        return trip;
    }

    /**
     * {@link TripStatus#DECIDED} 상태의 여행 및 여행에 소속된 Day들을 생성하고, 저장소에 저장하여 셋팅합니다.
     * @param tripperId 사용자(여행자)의 id
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 여행
     */
    protected Trip setupDecidedTrip(Long tripperId, LocalDate startDate, LocalDate endDate) {
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);
        trip.getDays().forEach(em::persist);
        return trip;
    }

    /**
     * 임시보관함 일정을 생성 및 저장하여 셋팅하고 그 일정을 반환합니다.
     * @param trip 일정이 소속된 여행
     * @param scheduleIndexValue 일정의 순서값({@link ScheduleIndex})의 원시값({@link Long})
     * @return 일정
     */
    protected Schedule setupTemporarySchedule(Trip trip, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }

    /**
     * 임시보관함 일정을 생성 및 저장하여 셋팅하고 그 일정을 반환합니다.
     * @param trip 일정이 소속된 여행
     * @param scheduleIndexValue 일정의 순서값({@link ScheduleIndex})의 원시값({@link Long})
     * @return 일정
     */
    protected Schedule setupDaySchedule(Trip trip, Day day, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.day_NullId(trip, day, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }
}
