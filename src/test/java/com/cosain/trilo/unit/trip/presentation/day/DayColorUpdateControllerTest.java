package com.cosain.trilo.unit.trip.presentation.day;


import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.day.dto.DayColorUpdateCommand;
import com.cosain.trilo.trip.application.day.dto.factory.DayColorUpdateCommandFactory;
import com.cosain.trilo.trip.application.day.service.DayColorUpdateService;
import com.cosain.trilo.trip.domain.vo.DayColor;
import com.cosain.trilo.trip.presentation.day.DayColorUpdateController;
import com.cosain.trilo.trip.presentation.day.dto.DayColorUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Day 색상 수정 컨트롤러 테스트")
@WebMvcTest(DayColorUpdateController.class)
public class DayColorUpdateControllerTest extends RestControllerTest {

    @MockBean
    private DayColorUpdateService dayColorUpdateService;

    @MockBean
    private DayColorUpdateCommandFactory dayColorUpdateCommandFactory;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("인증된 사용자의 DayColor 수정 요청 -> 성공")
    public void successTest() throws Exception {
        mockingForLoginUserAnnotation();

        // given
        Long dayId = 1L;
        String rawColorName = "RED";
        DayColorUpdateRequest request = new DayColorUpdateRequest(rawColorName);

        DayColor dayColor = DayColor.of(rawColorName);
        DayColorUpdateCommand command = new DayColorUpdateCommand(dayColor);

        // mocking
        given(dayColorUpdateCommandFactory.createCommand(eq(rawColorName)))
                .willReturn(command);

        willDoNothing()
                .given(dayColorUpdateService)
                .updateDayColor(eq(dayId), any(), any(DayColorUpdateCommand.class));


        // when & then
        mockMvc.perform(put("/api/days/{dayId}/color", dayId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(dayId));

        verify(dayColorUpdateCommandFactory, times(1)).createCommand(eq(rawColorName));
        verify(dayColorUpdateService, times(1)).updateDayColor(eq(dayId), any(), any(DayColorUpdateCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 요청 -> 인증 실패 401")
    @WithAnonymousUser
    public void updateDayColor_with_unauthorizedUser() throws Exception {
        Long dayId = 1L;
        String rawColorName = "RED";
        DayColorUpdateRequest request = new DayColorUpdateRequest(rawColorName);

        mockMvc.perform(put("/api/days/{dayId}/color", dayId)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(dayColorUpdateCommandFactory, times(0)).createCommand(eq(rawColorName));
        verify(dayColorUpdateService, times(0)).updateDayColor(eq(dayId), any(), any(DayColorUpdateCommand.class));
    }

    @Test
    @DisplayName("비어있는 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void updateDayColor_with_emptyContent() throws Exception {
        mockingForLoginUserAnnotation();

        // given
        Long dayId = 1L;
        String content = "";

        mockMvc.perform(put("/api/days/{dayId}/color", dayId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(content)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }

    @Test
    @DisplayName("형식이 올바르지 않은 바디 -> 올바르지 않은 요청 데이터 형식으로 간주하고 400 예외")
    public void createTrip_with_invalidContent() throws Exception {
        mockingForLoginUserAnnotation();

        Long dayId = 1L;
        String content = """
                {
                    "colorName": 따옴표 안 감싼 색상명
                }
                """;

        mockMvc.perform(put("/api/days/{dayId}/color", dayId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(content)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }


    @Test
    @DisplayName("DayId로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void updateDayColor_with_notNumberDayId() throws Exception {
        mockingForLoginUserAnnotation();

        String notNumberDayId = "가가가";
        DayColorUpdateRequest request = new DayColorUpdateRequest("RED");

        mockMvc.perform(put("/api/days/{dayId}/color", notNumberDayId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .content(createJson(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());
    }
}
