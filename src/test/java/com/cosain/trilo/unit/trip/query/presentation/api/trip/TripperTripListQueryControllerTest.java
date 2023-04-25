package com.cosain.trilo.unit.trip.query.presentation.api.trip;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.query.presentation.trip.TripperTripListQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("특정 사용자 여행 목록 API 테스트")
@WebMvcTest(TripperTripListQueryController.class)
class TripperTripListQueryControllerTest extends RestControllerTest {

    @Test
    @DisplayName("인증된 사용자 요청 -> 미구현 500")
    @WithMockUser
    public void findTripperTripList_with_authorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips?tripper-id=1"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void findTripperTripList_with_unauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/trips?tripper-id=1"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

}
