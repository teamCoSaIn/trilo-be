package com.cosain.trilo.trip.infra.repository.trip;

import com.cosain.trilo.trip.infra.dto.QTripDetail;
import com.cosain.trilo.trip.infra.dto.QTripSummary;
import com.cosain.trilo.trip.infra.dto.TripDetail;
import com.cosain.trilo.trip.infra.dto.TripSummary;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TripPageCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.cosain.trilo.trip.domain.entity.QTrip.trip;

@Repository
@RequiredArgsConstructor
public class TripQueryRepository{

    private final JPAQueryFactory query;

    /**
     * Entity 가 아닌 DTO 로 필요한 속성들만 조회, 프로젝션 활용
     * JPQL 을 사용할 수도 있으나,
     * 1. new 키워드 사용
     * 2. package 이름 다 적어야돼서 지저분함
     * 3. 생성자만 지원
     * 등의 단점을 극복하기 위해 QueryDSL 사용
     *
     */
    public Optional<TripDetail> findTripDetailById(Long tripId){

        return Optional.ofNullable(query.select(
                        new QTripDetail(trip.id, trip.tripperId, trip.tripTitle.value, trip.status, trip.tripPeriod.startDate, trip.tripPeriod.endDate))
                .from(trip)
                .where(trip.id.eq(tripId))
                .fetchOne());
    }

    public Slice<TripSummary> findTripSummariesByTripperId(TripPageCondition tripPageCondition, Pageable pageable) {
        JPAQuery<TripSummary> jpaQuery = query.select(new QTripSummary(trip.id, trip.tripperId, trip.tripTitle.value, trip.status, trip.tripPeriod.startDate, trip.tripPeriod.endDate))
                .from(trip)
                .where(
                        trip.tripperId.eq(tripPageCondition.getTripperId()),
                        ltTripId(tripPageCondition.getTripId())
                )
                .orderBy(trip.id.desc())
                .limit(pageable.getPageSize() + 1);

        List<TripSummary> result = jpaQuery.fetch();
        boolean hasNext = isHasNext(result, pageable);

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private boolean isHasNext(List<?> result, Pageable pageable){
        boolean hasNext = false;
        if(result.size() > pageable.getPageSize()){
            hasNext = true;
            result.remove(pageable.getPageSize());
        }
        return hasNext;
    }

    private BooleanExpression ltTripId(Long tripId){
        if(tripId == null) return null;
        return trip.id.lt(tripId);
    }

    public boolean existById(Long tripId) {
        Integer fetchOne = query.selectOne()
                .from(trip)
                .where(trip.id.eq(tripId))
                .fetchFirst();

        return fetchOne != null;
    }
}
