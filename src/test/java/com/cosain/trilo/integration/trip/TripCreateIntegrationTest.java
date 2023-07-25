package com.cosain.trilo.integration.trip;

import com.cosain.trilo.support.IntegrationTest;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripImage;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripStatus;
import com.cosain.trilo.trip.domain.vo.TripTitle;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripCreateRequest;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripCreateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 여행 생성 기능에 대한 통합 테스트 클래스입니다.
 */
@Slf4j
@DisplayName("[통합] 여행 생성 API 테스트")
public class TripCreateIntegrationTest extends IntegrationTest {

    /**
     * 테스트에서 여행의 저장 여부를 확인하기 위한 리포지토리 의존성
     */
    @Autowired
    private TripRepository tripRepository;


    /**
     * <p>여행 생성 요청을 했을 때, 여행생성 기능이 잘 동작하는 지 검증하고, 해당 API를 문서화합니다.</p>
     * <ul>
     *     <li>생성이 성공됐다는 응답이 와야합니다. (201 Created, 생성된 사용자 식별자)</li>
     *     <li>생성된 사용자가 실제 잘 저장되어 있는 지 검증해야합니다.</li>
     * </ul>
     */
    @DisplayName("여행 생성 -> 여행 생성 됨")
    @Test
    void createTest() throws Exception {
        // given
        User user = setupMockKakaoUser();
        String title = "여행 제목";
        var request = new TripCreateRequest(title);

        // when
        ResultActions resultActions = runTest(createRequestJson(request), user); // 인증된 사용자의 요청

        // then
        TripCreateResponse response = createResponseObject(resultActions, TripCreateResponse.class); // 응답 본문을 객체로 바인딩
        Trip createdTrip = tripRepository.findById(response.getTripId()).orElse(null); // 생성된 여행 조회

        // 응답 메시지 검증
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").isNotEmpty());

        // 생성된 Trip의 필드 검증
        assertThat(createdTrip).isNotNull();
        assertThat(createdTrip.getTripperId()).isEqualTo(user.getId());
        assertThat(createdTrip.getTripTitle()).isEqualTo(TripTitle.of(title));
        assertThat(createdTrip.getTripPeriod()).isEqualTo(TripPeriod.empty());
        assertThat(createdTrip.getTripImage()).isEqualTo(TripImage.defaultImage());
        assertThat(createdTrip.getStatus()).isSameAs(TripStatus.UNDECIDED);
        assertThat(createdTrip.getDays()).isEmpty();
        assertThat(createdTrip.getTemporaryStorage()).isEmpty();
    }

    /**
     * 인증된 사용자의 요청을 mocking하여 수행하고, 그 결과를 객체로 얻어옵니다.
     * @param content : 요청 본문(body)
     * @param requestUser : 요청 사용자
     * @return 실제 요청 실행 결과
     */
    private ResultActions runTest(String content, User requestUser) throws Exception {
        return mockMvc.perform(post("/api/trips")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader(requestUser))
                .content(content)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
