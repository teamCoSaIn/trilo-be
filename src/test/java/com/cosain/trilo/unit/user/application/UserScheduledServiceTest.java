package com.cosain.trilo.unit.user.application;

import com.cosain.trilo.user.application.UserScheduledService;
import com.cosain.trilo.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserScheduledServiceTest {

    @InjectMocks
    private UserScheduledService userScheduledService;
    @Mock
    private UserRepository userRepository;

    @Test
    void 정해진_시간에_회원_벌크_삭제_실행(){
        // when
        userScheduledService.deleteAllUserWithDelFlag();

        // then
        verify(userRepository, times(1)).deleteAllWhereIsDelTrue();
    }
}
