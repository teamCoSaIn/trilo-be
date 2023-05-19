package com.cosain.trilo.trip.query.infra.repository.day.jpa;

import com.cosain.trilo.trip.command.domain.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayQueryJpaRepository extends JpaRepository<Day, Long>, DayQueryJpaRepositoryCustom {

}
