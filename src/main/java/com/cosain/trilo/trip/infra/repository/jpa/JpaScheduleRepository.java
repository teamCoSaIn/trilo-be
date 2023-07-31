package com.cosain.trilo.trip.infra.repository.jpa;

import com.cosain.trilo.trip.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Schedule 엔티티를 관리하는 Spring Data JPA Repository
 */
public interface JpaScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * 인자로 전달받은 식별자의 일정(속한 여행 포함)을 담은 Optional을 조회하여 반환받습니다.
     * @param scheduleId 일정의 id(식별자)
     * @return 조회해온 일정(속한 여행 포함)을 담은 Optional(null 가능성 있음)
     * @see Optional
     */
    @Query("""
            SELECT s
            FROM Schedule as s JOIN FETCH s.trip
            WHERE s.id = :scheduleId
            """)
    Optional<Schedule> findByIdWithTrip(@Param("scheduleId") Long scheduleId);

    /**
     * Day또는 임시보관함의 일정들의 순서값을 일괄 재배치합니다.
     *
     * @param tripId 소속된 Trip의 Id
     * @param dayId  소속된 Day의 Id(null일 경우 임시보관함으로 간주)
     * @return 변경 영향을 받은 일정의 갯수
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
             -- 일정을 갱신하라
             UPDATE schedules s
             
             -- 일정의 순서값(ScheduleIndex)를
             SET s.schedule_index = (
                -- 자기 자신보다 큰 순서값을 가진 일정의 갯수에 1000만을 곱한 값으로
               SELECT subQuery.countValue * 10000000
               FROM (
                 SELECT COUNT(s2.schedule_id) AS countValue
                 FROM schedules s2
                 
                 -- 같은 Day 또는 임시보관함 내에서 일정 자신보다 순서값이 큰 일정의 갯수를 구함
                 WHERE (:dayId IS NOT NULL AND s2.day_id = :dayId AND s2.schedule_index < s.schedule_index)
                       OR (:dayId IS NULL AND s2.day_id IS NULL AND s2.trip_id = :tripId AND s2.schedule_index < s.schedule_index)
               ) AS subQuery
             )
             
             -- 여행 id가 같고, DayId가 같은 일정들을(DayId가 null 이면 임시보관함의 일정으로 간주함)
             WHERE s.trip_id = :tripId
               AND ((:dayId IS NOT NULL AND s.day_id = :dayId) OR (:dayId IS NULL AND s.day_id IS NULL))
            """, nativeQuery = true
    )
    int relocateDaySchedules(@Param("tripId") Long tripId, @Param("dayId") Long dayId);

    /**
     * 전달받은 Day들에 속한 일정들을 여행의 임시보관함 맨 뒤로 옮깁니다.
     * @param tripId 여행의 id
     * @param dayIds 일정들이 속한 Day들의 Id
     * @return 이동된 일정의 갯수
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
                WITH subquery1 AS (
                -- 날짜 순 오름차순(같으면 schedule_index 오름차순) 가져오고, 각 행에 순서 번호 부여(1,2,...)
                    SELECT
                        s2.schedule_id,
                        ROW_NUMBER() OVER (ORDER BY d.trip_date ASC, s2.schedule_index ASC) AS row_num
                    FROM
                        schedules s2
                        JOIN days d ON s2.day_id = d.day_id
                    WHERE
                        s2.day_id IN :dayIds
                ),
                subquery2 AS (
                -- 임시보관함의 최대 순서값(ScheduleIndex) 구하기
                    SELECT
                        MAX(s3.schedule_index) AS max_temporary_storage_schedule_index
                    FROM
                        schedules s3
                    WHERE
                        s3.trip_id = :tripId AND s3.day_id IS NULL
                )
                
                
                -- 실제 UPDATE 문 실행 : day들에 속한 일정들을 임시보관함의 제일 뒤로 옮기기
                UPDATE
                -- 일정들을 이동시켜라
                    schedules s1
                SET
                -- 임시보관함으로
                    s1.day_id = NULL,
                    
                    --  ScheduleIndex 변경 : 임시보관함 최대 순서값에 아래의 값을 더한 값으로 변경
                    s1.schedule_index = COALESCE((SELECT max_temporary_storage_schedule_index FROM subquery2), -10000000) +
                        -- subquery1에서 구한 행 번호에 1000만을 곱한 값
                        (SELECT row_num * 10000000 FROM subquery1 WHERE subquery1.schedule_id = s1.schedule_id)
                
                -- dayIds 의 Day에 속한 일정들을
                WHERE
                    s1.day_id IN :dayIds
            """, nativeQuery = true)
    int moveSchedulesToTemporaryStorage(@Param("tripId") Long tripId, @Param("dayIds") List<Long> dayIds);

    @Query("""
            SELECT COUNT(s)
            FROM Schedule as s
            WHERE s.trip.id = :tripId
            """)
    int findTripScheduleCount(@Param("tripId") Long tripId);

    /**
     * 전달받은 식별자의 Day가 가진 일정의 갯수를 반환받습니다.
     * @param dayId Day의 id
     * @return Day가 가진 일정의 갯수
     */
    @Query("""
            SELECT COUNT(s)
            FROM Schedule as s
            WHERE s.day.id = :dayId
            """)
    int findDayScheduleCount(@Param("dayId") Long dayId);

    /**
     * 전달받은 식별자의 여행(Trip)에 속해있는 일정 엔티티들을 모두 제거합니다.
     *
     * @param tripId 여행의 식별자(id)
     */
    @Modifying
    @Query("DELETE FROM Schedule as s where s.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);

    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.trip.id in :tripIdsForDelete")
    void deleteAllByTripIds(@Param("tripIdsForDelete") List<Long> tripIdsForDelete);
}
