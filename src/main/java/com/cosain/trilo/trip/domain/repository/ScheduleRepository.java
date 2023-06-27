package com.cosain.trilo.trip.domain.repository;

import com.cosain.trilo.trip.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s" +
            " FROM Schedule as s JOIN FETCH s.trip" +
            " WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithTrip(@Param("scheduleId") Long scheduleId);

    @Modifying
    @Query("DELETE FROM Schedule as s where s.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") Long tripId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE schedules s
            SET s.schedule_index = (
              SELECT subQuery.countValue * 10000000
              FROM (
                SELECT COUNT(s2.schedule_id) AS countValue
                FROM schedules s2
                WHERE (:dayId IS NOT NULL AND s2.day_id = :dayId AND s2.schedule_index < s.schedule_index)
                      OR (:dayId IS NULL AND s2.day_id IS NULL AND s2.trip_id = :tripId AND s2.schedule_index < s.schedule_index)
              ) AS subQuery
            )
            WHERE s.trip_id = :tripId
              AND ((:dayId IS NOT NULL AND s.day_id = :dayId) OR (:dayId IS NULL AND s.day_id IS NULL))
           """, nativeQuery = true
    )
    int relocateDaySchedules(@Param("tripId") Long tripId, @Param("dayId") Long dayId);

    @Modifying(clearAutomatically = true)
    @Query(value = "WITH subquery1 AS (" + // 날짜 순 오름차순(같으면 schedule_index 오름차순) 가져오고, 각 행에 순서 번호 부여
            "   SELECT " +
            "       s2.schedule_id, " +
            "       ROW_NUMBER() OVER (ORDER BY d.trip_date ASC, s2.schedule_index ASC) AS row_num " +
            "   FROM " +
            "       schedules s2 " +
            "       JOIN days d ON s2.day_id = d.day_id " +
            "   WHERE " +
            "       s2.day_id IN :dayIds" +
            "), " +
            "subquery2 AS (" +
            "   SELECT " +
            "       MAX(s3.schedule_index) AS max_temporary_storage_schedule_index " + // 임시보관함의 최대 인덱스값 구하기
            "   FROM " +
            "       schedules s3 " +
            "   WHERE " +
            "       s3.trip_id = :tripId AND s3.day_id IS NULL" +
            ") " +
            "UPDATE " +
            "   schedules s1 " +
            "SET " + // 임시보관함의 제일 뒤로 옮기기
            "   s1.day_id = NULL, " +
            "   s1.schedule_index = COALESCE((SELECT max_temporary_storage_schedule_index FROM subquery2), -10000000) + " +
            "       (SELECT row_num * 10000000 FROM subquery1 WHERE subquery1.schedule_id = s1.schedule_id) " +
            "WHERE " +
            "   s1.day_id IN :dayIds", nativeQuery = true)
    int moveSchedulesToTemporaryStorage(@Param("tripId") Long tripId, @Param("dayIds") List<Long> dayIds);

    @Query("""
            SELECT COUNT(s)
            FROM Schedule as s
            WHERE s.trip.id = :tripId
            """)
    int findTripScheduleCount(@Param("tripId") Long tripId);

    @Query("""
            SELECT COUNT(s)
            FROM Schedule as s
            WHERE s.day.id = :dayId
            """)
    int findDayScheduleCount(@Param("dayId") Long dayId);
}
