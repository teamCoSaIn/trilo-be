package com.cosain.trilo.trip.infra.repository.trip.jpa;

import com.cosain.trilo.trip.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripQueryJpaRepository extends JpaRepository<Trip, Long>, TripQueryJpaRepositoryCustom {

}
