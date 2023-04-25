package com.cosain.trilo.unit.trip.query.presentation.api.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.query.presentation.trip.TripTemporaryStorageQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("여행의 임시보관함 조회 API 테스트")
@WebMvcTest(TripTemporaryStorageQueryController.class)
class TripTemporaryStorageQueryControllerTest extends RestControllerTest {

    @Test
    @DisplayName("인증된 사용자 요청 -> 미구현 500")
    @WithMockUser
    public void findTripTemporaryStorage_with_authorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips/1/temporary-storage"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findTripTemporaryStorage_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips/1/temporary-storage"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }
}
