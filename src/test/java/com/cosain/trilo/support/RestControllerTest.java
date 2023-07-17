package com.cosain.trilo.support;

import com.cosain.trilo.auth.infra.token.JwtProviderImpl;
import com.cosain.trilo.auth.infra.token.UserPayload;
import com.cosain.trilo.common.logging.query.QueryCounter;
import com.cosain.trilo.config.MessageSourceTestConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@Import({MessageSourceTestConfig.class, QueryCounter.class})
public class RestControllerTest {

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @MockBean
    protected JwtProviderImpl jwtProvider;

    protected String createJson(Object dto) throws JsonProcessingException{
        return objectMapper.writeValueAsString(dto);
    }

    protected void mockingForLoginUserAnnotation(Long id){
        given(jwtProvider.isValidAccessToken(any())).willReturn(true);
        given(jwtProvider.getPayload(any())).willReturn(new UserPayload(id));
    }

    protected void mockingForLoginUserAnnotation(){
        given(jwtProvider.isValidAccessToken(any())).willReturn(true);
        given(jwtProvider.getPayload(any())).willReturn(new UserPayload(1L));
    }

}
