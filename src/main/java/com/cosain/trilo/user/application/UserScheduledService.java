package com.cosain.trilo.user.application;

import com.cosain.trilo.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserScheduledService {

    private final UserRepository userRepository;

    // 매주 월요일 오전 5시
    @Scheduled(cron = "0 0 5 ? * MON")
    @Transactional
    public void deleteAllUserWithDelFlag(){
        userRepository.deleteAllWhereIsDelTrue();
    }
}
