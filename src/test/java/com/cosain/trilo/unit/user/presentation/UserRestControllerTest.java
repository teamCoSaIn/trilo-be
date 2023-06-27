package com.cosain.trilo.unit.user.presentation;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.user.presentation.UserRestController;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTest extends RestControllerTest {

    private final String BASE_URL = "/api/users";
    private final String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    public void 인증된_사용자_프로필_조회시_200() throws Exception{
        // given
        mockingForLoginUserAnnotation();
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/profile")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void 미인증된_사용자_프로필_조회시_401() throws Exception{
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/profile")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }
}
