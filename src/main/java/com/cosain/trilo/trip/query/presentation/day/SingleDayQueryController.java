package com.cosain.trilo.trip.query.presentation.day;

import com.cosain.trilo.common.exception.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 일정 단건 조회
 */
@Slf4j
@RestController
public class SingleDayQueryController {

    @GetMapping("/api/days/{dayId}")
    public String findSingleDay(@PathVariable Long dayId) {
        throw new NotImplementedException("Day 단건 조회 미구현");
    }
}
