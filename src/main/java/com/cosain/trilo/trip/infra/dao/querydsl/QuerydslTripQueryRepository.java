package com.cosain.trilo.trip.infra.dao.querydsl;

import com.cosain.trilo.trip.application.trip.service.trip_detail_search.QTripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_detail_search.TripDetail;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.QTripListSearchResult_TripSummary;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListQueryParam;
import com.cosain.trilo.trip.application.trip.service.trip_list_search.TripListSearchResult;
import com.cosain.trilo.trip.infra.dto.QTripStatistics;
import com.cosain.trilo.trip.infra.dto.TripStatistics;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.cosain.trilo.trip.domain.entity.QTrip.trip;

@Component
@RequiredArgsConstructor
public class QuerydslTripQueryRepository {

    private final JPAQueryFactory query;

    /**
     * Entity 가 아닌 DTO 로 필요한 속성들만 조회, 프로젝션 활용
     * JPQL 을 사용할 수도 있으나,
     * 1. new 키워드 사용
     * 2. package 이름 다 적어야돼서 지저분함
     * 3. 생성자만 지원
     * 등의 단점을 극복하기 위해 QueryDSL 사용
     */
    public Optional<TripDetail> findTripDetailById(Long tripId) {
        return Optional.ofNullable(query.select(new QTripDetail(trip.id, trip.tripperId, trip.tripTitle.value, trip.status, trip.tripPeriod.startDate, trip.tripPeriod.endDate))
                .from(trip)
                .where(trip.id.eq(tripId))
                .fetchOne());
    }

    public TripListSearchResult findTripSummariesByTripperId(TripListQueryParam queryParam) {
        List<TripListSearchResult.TripSummary> result = query.select(new QTripListSearchResult_TripSummary(trip.id, trip.tripperId, trip.tripTitle.value, trip.status, trip.tripPeriod.startDate, trip.tripPeriod.endDate, trip.tripImage.fileName))
                .from(trip)
                .where(
                        trip.tripperId.eq(queryParam.getTripperId()),
                        ltTripId(queryParam.getTripId())
                )
                .orderBy(trip.id.desc())
                .limit(queryParam.getPageSize() + 1)
                .fetch();

        Pageable pageable = PageRequest.ofSize(queryParam.getPageSize());
        boolean hasNext = isHasNext(result, pageable);
        Slice<TripListSearchResult.TripSummary> slice = new SliceImpl<>(result, pageable, hasNext);
        return TripListSearchResult.of(slice.hasNext(), result);
    }

    /**
     * @param result
     * @param pageable 다음 요청값이 있는지 없는지를 반환해주는 메서드입니다. 메서드를 호출하기 전에 실제 요청 size + 1 만큼 조회 쿼리를 날린 다음
     *                 실제 size + 1 만큼의 데이터를 결과(result)로 가져온다면 결국 다음 요청할 데이터가 존재한다는 것이므로 hasNext 를 true로
     *                 설정한다음 반환하게 됩니다. 동시에 실제 요청 크기(pageable.getPageSize())보다 +1 만큼 조회했으므로
     *                 remove() 메서드를 통해 리스트에 담긴 중에서 마지막 데이터를 제거합니다.
     */
    private boolean isHasNext(List<?> result, Pageable pageable) {
        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            result.remove(pageable.getPageSize());
        }
        return hasNext;
    }

    private BooleanExpression ltTripId(Long tripId) {
        return tripId == null
                ? null
                : trip.id.lt(tripId);
    }

    public boolean existById(Long tripId) {
        Integer fetchOne = query.selectOne()
                .from(trip)
                .where(trip.id.eq(tripId))
                .fetchFirst();

        return fetchOne != null;
    }

    public TripStatistics findTripStaticsByTripperId(Long tripperId, LocalDate today) {
        JPQLQuery<Long> subQuery = JPAExpressions.select(trip.count())
                .from(trip)
                .where(trip.tripPeriod.endDate.before(today));

        TripStatistics tripStatistics = query.select(new QTripStatistics(trip.count(), subQuery))
                .from(trip)
                .where(trip.tripperId.eq(tripperId))
                .fetchOne();

        return tripStatistics;
    }
}
