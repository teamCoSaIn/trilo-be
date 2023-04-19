package com.cosain.trilo.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.TripUpdateCommand;
import com.cosain.trilo.trip.command.application.usecase.TripUpdateUseCase;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripUpdateService implements TripUpdateUseCase {

    private final TripRepository tripRepository;

    @Override
    public void updateTrip(Long tripId, Long tripperId, TripUpdateCommand updateCommand) {

        //TODO: 변경하고자 하는 사용자가 Trip의 소유주와 같은 지 검증

        //TODO: 제목 변경

        //TODO: 기간 변경

        //TODO: 기간변경의 파급효과 -> 소속한 Day 추가/삭제

        //TODO: 데이터베이스에 반영(영속성 컨텍스트의 변경감지 기능을 사용하거나, 쿼리를 작성하거나)
    }

}
