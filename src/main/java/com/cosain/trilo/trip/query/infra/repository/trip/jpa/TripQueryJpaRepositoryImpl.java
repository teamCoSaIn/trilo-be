package com.cosain.trilo.trip.query.infra.repository.trip.jpa;

import com.cosain.trilo.trip.query.infra.dto.QTripDetail;
import com.cosain.trilo.trip.query.infra.dto.TripDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.cosain.trilo.trip.command.domain.entity.QTrip.trip;

@RequiredArgsConstructor
public class TripQueryJpaRepositoryImpl implements TripQueryJpaRepositoryCustom{

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
                new QTripDetail(trip.id, trip.tripperId, trip.title, trip.status, trip.tripPeriod.startDate, trip.tripPeriod.endDate))
                .from(trip)
                .where(trip.id.eq(tripId))
                .fetchOne());
    }
}
