package com.cosain.trilo.support;

import com.cosain.trilo.auth.infra.token.JwtProviderImpl;
import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;
import com.cosain.trilo.user.domain.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private JwtProviderImpl jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected EntityManager em;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    protected void flushAndClear(){
        em.flush();
        em.clear();
    }

    protected User setupMockNaverUser() {
        return createMockUser("naver-user@naver.com", AuthProvider.NAVER);
    }

    protected User setupMockKakaoUser() {
        return createMockUser("kakao-user@kakao.com", AuthProvider.KAKAO);
    }

    protected User setupMockGoogleUser() {
        return createMockUser("google-user@google.com", AuthProvider.GOOGLE);
    }

    protected String authorizationHeader(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getId());
        return String.format("Bearer %s",  accessToken);
    }

    private User createMockUser(String email, AuthProvider authProvider) {
        User mockUser = User.builder()
                .nickName("사용자")
                .email(email)
                .profileImageUrl("https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .authProvider(authProvider)
                .role(Role.MEMBER)
                .build();

        userRepository.save(mockUser);
        return mockUser;
    }

    protected String createRequestJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    protected <T> T createResponseObject(ResultActions resultActions, Class<T> clazz) throws UnsupportedEncodingException, JsonProcessingException {
        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonResponse, clazz);
    }

    /**
     * {@link TripStatus#UNDECIDED} 상태의 여행을 생성하고, 저장소에 저장하여 셋팅합니다.
     * @param tripperId 사용자(여행자)의 id
     * @return 여행
     */
    protected Trip setupUndecidedTrip(Long tripperId) {
        Trip trip = TripFixture.undecided_nullId(tripperId);
        em.persist(trip);
        return trip;
    }

    /**
     * {@link TripStatus#DECIDED} 상태의 여행 및 여행에 소속된 Day들을 생성하고, 저장소에 저장하여 셋팅합니다.
     * @param tripperId 사용자(여행자)의 id
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 여행
     */
    protected Trip setupDecidedTrip(Long tripperId, LocalDate startDate, LocalDate endDate) {
        Trip trip = TripFixture.decided_nullId(tripperId, startDate, endDate);
        em.persist(trip);
        trip.getDays().forEach(em::persist);
        return trip;
    }

    /**
     * 임시보관함 일정을 생성 및 저장하여 셋팅하고 그 일정을 반환합니다.
     * @param trip 일정이 소속된 여행
     * @param scheduleIndexValue 일정의 순서값({@link ScheduleIndex})의 원시값({@link Long})
     * @return 일정
     */
    protected Schedule setupTemporarySchedule(Trip trip, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.temporaryStorage_NullId(trip, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }

    /**
     * 임시보관함 일정을 생성 및 저장하여 셋팅하고 그 일정을 반환합니다.
     * @param trip 일정이 소속된 여행
     * @param scheduleIndexValue 일정의 순서값({@link ScheduleIndex})의 원시값({@link Long})
     * @return 일정
     */
    protected Schedule setupDaySchedule(Trip trip, Day day, long scheduleIndexValue) {
        Schedule schedule = ScheduleFixture.day_NullId(trip, day, scheduleIndexValue);
        em.persist(schedule);
        return schedule;
    }

}
